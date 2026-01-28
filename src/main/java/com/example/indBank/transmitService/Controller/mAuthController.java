package com.example.indBank.transmitService.Controller;

import com.example.indBank.transmitService.POJO.MAuthRequest;
import com.example.indBank.transmitService.POJO.MAuthUserRequest;
import com.example.indBank.transmitService.POJO.MAuth_DeprovisionRequest;
import com.example.indBank.transmitService.Service.MAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class mAuthController
{
    @Autowired
    private MAuthService mAuthService;


    @PostMapping(value = "/Identity/indBank/mauth/v1/dsp/auth")
    public ResponseEntity<Map<String, Object>> mAuth_authenticate(
            @RequestParam(value = "_service") String service,
            @RequestHeader(name = "x-channel") String channel,
            @RequestHeader(name = "x-group-member") String group_member,
            @RequestHeader(name = "x-correlationId") String correlationId,
            @RequestHeader(name = "x-country") String country,
            @RequestBody MAuthRequest mAuthRequest,
            HttpServletResponse httpServletResponse
    )
    {
        if(service==null || service.isEmpty() || !service.equalsIgnoreCase("mAuthService"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid _service parameter value provided!");
        }
        if(channel==null || channel.isEmpty() || !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(group_member==null || group_member.isEmpty() || !group_member.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        if (correlationId==null || correlationId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-correlationId!");
        }
        if(country==null || country.isEmpty() || !country.equalsIgnoreCase("INDIA"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-country!");
        }
        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(correlationId);
        if(!m.matches())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "x-correlationId should be alpha-numeric");
        }
        String deviceId = mAuthRequest.getCallbacksValue1().getValue();
        String customerId = mAuthRequest.getCallbacksValue2().getValue();
        String mPIN = mAuthRequest.getCallbacksValue3().getValue();
        if(!mAuthRequest.getCallbacksInput1().getName().equals("deviceId")
                || !mAuthRequest.getCallbacksInput2().getName().equals("customerId")
                || !mAuthRequest.getCallbacksInput3().getName().equals("mPIN"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Callbacks error: Invalid callback input name!");
        }
        else if(deviceId==null || deviceId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "DeviceId cannot be null or empty!");
        }
        else if(customerId==null || customerId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CustomerId cannot be null or empty!");
        }
        else if(mPIN==null || mPIN.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "mPIN cannot be null or empty!");
        }
        Map<String, Object> result =
                mAuthService.authenticate(mPIN, customerId, deviceId, httpServletResponse);
        if(result!=null)
        {
            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping(value = "/Identity/indBank/mauth/v1/dsp/provision")
    public ResponseEntity<Map<String, Object>> mAuth_profile_provision(
            @RequestParam(value = "_action") String action,
            @RequestHeader(name = "x-channel") String channel,
            @RequestHeader(name = "x-group-member") String group_member,
            @RequestHeader(name = "dspSession") String tokenId,
            @RequestHeader(name = "x-country") String country,
            @RequestBody MAuthRequest mAuthRequest)
    {
        if(!action.equalsIgnoreCase("translate"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid _action parameter value provided!");
        }
        if(channel==null || channel.isEmpty() || !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(group_member==null || group_member.isEmpty() || !group_member.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        if(country==null || country.isEmpty() || !country.equalsIgnoreCase("INDIA"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-country!");
        }
        if (tokenId==null || tokenId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(!mAuthRequest.getCallbacksInput1().getName().equals("deviceId")
                || !mAuthRequest.getCallbacksInput2().getName().equals("customerId")
                || !mAuthRequest.getCallbacksInput3().getName().equals("mPIN"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Callbacks error: Invalid callback input name!");
        }
        Map<String, Object> result =
                mAuthService.registerDevice(tokenId, mAuthRequest.getCallbacksValue1().getValue(), mAuthRequest.getCallbacksValue2().getValue(), mAuthRequest.getCallbacksValue3().getValue());
        if(result!=null)
        {
            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/Identity/indBank/mauth/v1/dsp/getUser")
    public ResponseEntity<Map<String, Object>> mAuth_User(@RequestParam(value = "_service") String service, @RequestHeader(value = "x-channel") String channel, @RequestHeader(value = "x-group-member") String groupMember, @RequestBody MAuthUserRequest mAuthUserRequest)
    {
        if(service==null || service.isEmpty() || !service.equalsIgnoreCase("mAuthUserAuthService"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid _service parameter value provided!");
        }
        if(channel==null || channel.isEmpty() || !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(groupMember==null || groupMember.isEmpty() || !groupMember.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        if(!mAuthUserRequest.getCallbacksInput1().getName().equals("deviceId")
                || !mAuthUserRequest.getCallbacksInput2().getName().equals("customerId"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Callbacks error: Invalid callback input name!");
        }
        String deviceId = mAuthUserRequest.getCallbacksValue1().getValue();
        String customerId = mAuthUserRequest.getCallbacksValue2().getValue();
        if(customerId==null || customerId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CustomerId cannot be null or empty!");
        }
        else if(deviceId==null || deviceId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "deviceId cannot be null or empty!");
        }
        Map<String, Object> result =
                mAuthService.getMauthUser(deviceId, customerId);
        if(result!=null)
        {
            return ResponseEntity.ok(result);
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping(value = "/Identity/indBank/mauth/v1/dsp/user/deprovision")
    public ResponseEntity<Map<String, Object>> deprovisionUser(@RequestHeader(value = "dspSession") String tokenId_header,@RequestHeader(value = "deviceId") String deviceId, @RequestBody MAuth_DeprovisionRequest mAuthDeprovisionRequest)
    {
        if(tokenId_header==null || tokenId_header.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be empty or null!");
        }
        if(!tokenId_header.equals(mAuthDeprovisionRequest.getTokenId()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
        if(deviceId==null || deviceId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deviceId cannot be empty or null!");
        }
        else if(mAuthDeprovisionRequest.getTokenId()==null || mAuthDeprovisionRequest.getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be empty or null!");
        }
        Map<String, Object> result = mAuthService.deprovision_profile(mAuthDeprovisionRequest.getTokenId(), deviceId);
        if(result!=null)
        {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
