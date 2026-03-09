package com.example.indBank.transmitService.Service;

import com.example.indBank.transmitService.Entity.mAuthEntity;
import com.example.indBank.transmitService.Utility.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OTP_Generate
{
    @Autowired
    mAuthRepo mAuthRepo;

    private static final Logger logger = LogManager.getLogger(OTP_Generate.class);

    public Map<String, Object> generateOTP(String deviceId, String customerId, String mPIN)
    {
        String OTP = "";
        Optional<mAuthEntity> authEntity = mAuthRepo.findByCustomerIdAndDeviceId(customerId, deviceId);
        if(authEntity.isPresent()) {
            mAuthEntity entity = authEntity.get();
            if (entity.getmPIN().equals(mPIN)) {
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
                headers.add("x-channel", "WEB");
                headers.add("x-group-member", "INDBANK");

                HttpEntity<Map<String, String>> req_entity = new HttpEntity<>(headers);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        req_entity,
                        new ParameterizedTypeReference<>() {
                        }
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> responseBody = response.getBody();
                    if (!responseBody.isEmpty()) {
                        OTP = responseBody.get("otp").toString();
                        String issuedAt = responseBody.get("issuedAt").toString();
                        logger.info(responseBody);
                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("otp", OTP);
                        result.put("issuedAt", issuedAt);

                        return result;

                    }
                }
                else
                {
                    Integer status_code = response.getStatusCode().value();
                    throw new ResponseStatusException(HttpStatusCode.valueOf(status_code), response.getBody().toString());
                }
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect mPIN!");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect profile and device details!");
        }
        return null;
    }
}
