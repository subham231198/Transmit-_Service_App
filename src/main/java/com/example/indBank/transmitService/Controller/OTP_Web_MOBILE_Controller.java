package com.example.indBank.transmitService.Controller;

import com.example.indBank.transmitService.POJO.MAuthRequest;
import com.example.indBank.transmitService.Service.OTP_Generate;
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
public class OTP_Web_MOBILE_Controller {

    @Autowired
    OTP_Generate otpGenerateService;

    @PostMapping(value = "/Identity/indBank/mauth/v1/dsp/getOTP")
    public ResponseEntity<Map<String, Object>> get_otp_inernet_banking(
            @RequestHeader(name = "x-channel") String channel,
            @RequestHeader(name = "x-group-member") String group_member,
            @RequestHeader(name = "x-country") String country,
            @RequestBody MAuthRequest mAuthRequest
    )
    {
        if(channel==null || channel.isEmpty() || !channel.equalsIgnoreCase("MOBILE") && !channel.equalsIgnoreCase("WEB"))
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
                otpGenerateService.generateOTP(deviceId, customerId, mPIN);
        if(result!=null)
        {
            return ResponseEntity.ok(result);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }
    }
}
