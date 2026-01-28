package com.example.indBank.transmitService.Service;


import com.example.indBank.transmitService.Entity.mAuthEntity;
import com.example.indBank.transmitService.Utility.ConfigReader;
import com.example.indBank.transmitService.Utility.Generator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Transactional
public class MAuthService
{
    @Autowired
    private mAuthRepo mAuthRepo;

    private static final Logger logger = LogManager.getLogger(MAuthService.class);

    public Map<String, Object> authenticate(String mPIN, String customerId, String deviceId, HttpServletResponse httpServletResponse)
    {
        String OTP = "";
        Optional<mAuthEntity> authEntity = mAuthRepo.findByCustomerIdAndDeviceId(customerId, deviceId);
        if(authEntity.isPresent())
        {
            mAuthEntity entity = authEntity.get();
            if(entity.getmPIN().equals(mPIN))
            {
                RestTemplate restTemplate = new RestTemplate();
                String dpCloud_url = ConfigReader.getHost("SelfBuild_host", "DomainHosts") +
                        ConfigReader.getURL("testingServices", "SelfBuild");
                URI uri = UriComponentsBuilder
                        .fromUriString(dpCloud_url)
                        .queryParam("customerId", customerId)
                        .build()
                        .encode()
                        .toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                headers.add("x-channel", "MOBILE");
                headers.add("x-group-member", "INDBANK");

                HttpEntity<Map<String, String>> req_entity = new HttpEntity<>(headers);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        req_entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                if(response.getStatusCode().is2xxSuccessful())
                {
                    Map<String, Object> responseBody = response.getBody();
                    if(!responseBody.isEmpty())
                    {
                        OTP = responseBody.get("otp").toString();
                        logger.info("OTP  = "+OTP);
                    }

                    String otpauth_url = ConfigReader.getHost("SelfBuild_host", "DomainHosts") +
                            ConfigReader.getURL("otpAuthService", "SelfBuild");

                    URI uri_otpAuth = UriComponentsBuilder
                            .fromUriString(otpauth_url)
                            .queryParam("_service", "OtpAuthService")
                            .build()
                            .encode()
                            .toUri();

                    HttpHeaders headers1 = new HttpHeaders();
                    headers1.setContentType(MediaType.APPLICATION_JSON);
                    headers1.setAccept(List.of(MediaType.APPLICATION_JSON));
                    headers1.add("x-channel", "MOBILE");
                    headers1.add("x-group-member", "INDBANK");
                    String correlationId = Generator.generateFraudSessionId();
                    headers1.add("x-correlationId", correlationId);
                    headers1.add("x-country", "INDIA");

                    Map<String, Object> payload = new HashMap<>();

                    Map<String, String> callbacksInput1 = new HashMap<>();
                    callbacksInput1.put("name", "customerId");

                    Map<String, String> callbacksValue1 = new HashMap<>();
                    callbacksValue1.put("value", customerId);

                    Map<String, String> callbacksInput2 = new HashMap<>();
                    callbacksInput2.put("name", "otp");

                    Map<String, String> callbacksValue2 = new HashMap<>();
                    callbacksValue2.put("value", OTP);

                    payload.put("callbacksInput1", callbacksInput1);
                    payload.put("callbacksValue1", callbacksValue1);
                    payload.put("callbacksInput2", callbacksInput2);
                    payload.put("callbacksValue2", callbacksValue2);

                    HttpEntity<Map<String, Object>> oto_entity = new HttpEntity<>(payload, headers1);

                    ResponseEntity<Map<String, Object>> response_session = restTemplate.exchange(
                            uri_otpAuth,
                            HttpMethod.POST,
                            oto_entity,
                            new ParameterizedTypeReference<Map<String, Object>>() {}
                    );
                    if(response_session.getStatusCode().is2xxSuccessful())
                    {
                        Map<String, Object> responseBody_session = response_session.getBody();
                        String dspSession = responseBody_session.get("tokenId").toString();
                        Cookie cookie = new Cookie(
                                "dspSession", dspSession
                        );

                        cookie.setHttpOnly(true);
                        cookie.setSecure(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(300);
                        httpServletResponse.addCookie(cookie);
                        return responseBody_session;
                    }
                    else
                    {
                        Map<String, Object> responseBody_session = response_session.getBody();
                        Integer code = Integer.parseInt(responseBody_session.get("code").toString());
                        String message = responseBody_session.get("message").toString();
                        throw new ResponseStatusException(HttpStatus.valueOf(code), message);
                    }

                }

            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mPIN!");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device not registered for this customerId!");
        }
        return null;
    }

    public Map<String, Object> registerDevice(String dspSession, String deviceId, String customerId, String mPIN)
    {
        ResponseEntity<Map<String, Object>> sessionResponse = sessionAttributesInfo(dspSession);
        if (!sessionResponse.getStatusCode().is2xxSuccessful() || sessionResponse.getBody() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve session information");
        }

        Map<String, Object> session_responseBody = sessionResponse.getBody();

        if (Boolean.FALSE.equals(session_responseBody.get("valid")))
        {
            return Map.of("valid", false);
        }

        if (Boolean.TRUE.equals(session_responseBody.get("isSessionValid"))) {
            String expected_customerId = session_responseBody.get("customerId").toString();
            logger.info("Expected CustomerId from session: " + expected_customerId);
            Assert.isTrue(expected_customerId.equals(customerId), "CustomerId does not match with session!");
            logger.info("CustomerId for adding DPCloudProfile: " + customerId);
            Map<String, Object> sessionBody = sessionResponse.getBody();
            Boolean isSessionValid = Boolean.parseBoolean(sessionBody.get("isSessionValid").toString());
            Assert.isTrue(isSessionValid, "Invalid dspSession provided!");
            Optional<mAuthEntity> authEntity = mAuthRepo.findByCustomerIdAndDeviceId(customerId, deviceId);
            if(authEntity.isPresent())
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device already registered for this customerId!");
            }
            else
            {
                Date issuedDate = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(issuedDate);
                calendar.add(Calendar.DAY_OF_MONTH, 90);
                Date expiryDate = calendar.getTime();
                mAuthEntity entity = new mAuthEntity();
                entity.setCustomerId(customerId);
                entity.setDeviceId(deviceId);
                entity.setmPIN(mPIN);
                entity.setIs_profile_provisioned(true);
                entity.setProfile_provisioned_timeStamp(new Date().toString());
                entity.setmPIN_issued_timeStamp(new Date().toString());
                entity.setmPIN_expiry_timeStamp(expiryDate.toString());
                mAuthRepo.save(entity);

                RestTemplate restTemplate = new RestTemplate();
                String addDPCloud_URL = ConfigReader.getHost("SelfBuild_host", "DomainHosts") +
                        ConfigReader.getURL("addDPCloudProfile", "SelfBuild");
                URI uri = UriComponentsBuilder
                        .fromUriString(addDPCloud_URL)
                        .build()
                        .encode()
                        .toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                Map<String, String> payload = new HashMap<>();
                payload.put("tokenId", dspSession);

                HttpEntity<Map<String, String>> req_entity = new HttpEntity<>(payload, headers);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        uri,
                        HttpMethod.POST,
                        req_entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );
                if(!response.getStatusCode().is2xxSuccessful())
                {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device registration failed at DPCloud!");
                }
                Map<String, Object> responseBody = response.getBody();
                String message = responseBody.get("message").toString();
                if(message.contains("OTP profile successfully created for customerId"))
                {
                    return Map.of("registration", "successful");
                }
                else
                {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Device registration failed at DPCloud!");
                }
            }
        }

        return Map.of("valid", false);

    }

    public static ResponseEntity<Map<String, Object>> sessionAttributesInfo(String tokenId) {

        RestTemplate restTemplate = new RestTemplate();

        String url = ConfigReader.getHost("SelfBuild_host", "DomainHosts")
                + ConfigReader.getURL("getSessionInfo", "SelfBuild");

        URI uri = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("_action", "getSessionInfo")
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("x-channel", "MOBILE");
        headers.add("x-group-member", "INDBANK");
        headers.add("dspSession", tokenId);

        Map<String, String> requestBody = Map.of("tokenId", tokenId);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    public Map<String, Object> getMauthUser(String deviceId, String customerId)
    {
        Optional<mAuthEntity> authEntity = mAuthRepo.findByCustomerIdAndDeviceId(customerId, deviceId);
        if(authEntity.isPresent())
        {
            mAuthEntity entity = authEntity.get();
            Map<String, Object> response = new HashMap<>();
            response.put("customerId", entity.getCustomerId());
            response.put("deviceId", entity.getDeviceId());
            response.put("mPIN", entity.getmPIN());
            response.put("is_profile_provisioned", entity.getIs_profile_provisioned());
            response.put("mPIN_issued_timeStamp", entity.getmPIN_issued_timeStamp());
            response.put("mPIN_expiry_timeStamp", entity.getmPIN_expiry_timeStamp());
            return response;
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No mAuth user found for the given customerId and deviceId!");
        }
    }

    public Map<String, Object> deprovision_profile(String tokenId, String deviceId) {
        ResponseEntity<Map<String, Object>> sessionResponse = sessionAttributesInfo(tokenId);
        if (!sessionResponse.getStatusCode().is2xxSuccessful() || sessionResponse.getBody() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve session information");
        }

        Map<String, Object> session_responseBody = sessionResponse.getBody();

        if (Boolean.FALSE.equals(session_responseBody.get("valid"))) {
            return Map.of("valid", false);
        }

        if (Boolean.TRUE.equals(session_responseBody.get("isSessionValid"))) {
            String customerId = session_responseBody.get("customerId").toString();
            logger.info("CustomerId for removing DPCloudProfile: " + customerId);
            Boolean isSessionValid = Boolean.parseBoolean(session_responseBody.get("isSessionValid").toString());
            Assert.isTrue(isSessionValid, "Invalid dspSession provided!");
            Optional<mAuthEntity> authEntity = mAuthRepo.findByCustomerIdAndDeviceId(customerId, deviceId);
            if (authEntity.isPresent()) {
                mAuthRepo.deleteByCustomerId(customerId);
                String removeDPCloudProfileURL = ConfigReader.getHost("SelfBuild_host", "DomainHosts") + ConfigReader.getURL("removeDPCloudProfile", "SelfBuild");

                RestTemplate restTemplate = new RestTemplate();
                URI uri = UriComponentsBuilder
                        .fromUriString(removeDPCloudProfileURL)
                        .build()
                        .encode()
                        .toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                headers.add("x-channel", "MOBILE");
                headers.add("x-group-member", "INDBANK");
                headers.add("dspSession", tokenId);

                Map<String, String> requestBody = Map.of("tokenId", tokenId);

                HttpEntity<Map<String, String>> entity =
                        new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map<String, Object>> outcome = restTemplate.exchange(
                        uri,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );
                if (!sessionResponse.getStatusCode().is2xxSuccessful() || sessionResponse.getBody() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve session information");
                }
                Map<String, Object> responsebody = outcome.getBody();

                if (Boolean.FALSE.equals(responsebody.get("valid"))) {
                    return Map.of("valid", false);
                }
                else if (responsebody.get("message").equals("OTP profile successfully removed for customerId = " + customerId)) {
                    authEntity.get().setIs_profile_provisioned(false);
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("deviceId", authEntity.get().getDeviceId());
                    response.put("customerId", authEntity.get().getCustomerId());
                    response.put("isProfileProvisioned", authEntity.get().getIs_profile_provisioned());
                    return response;
                }

            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid deviceId or customerId");
            }

        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
    }
}
