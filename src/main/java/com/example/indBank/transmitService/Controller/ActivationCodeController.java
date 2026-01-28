package com.example.indBank.transmitService.Controller;

import com.example.indBank.transmitService.POJO.ActivationCodeRequest;
import com.example.indBank.transmitService.POJO.CodeAutenticateRequest;
import com.example.indBank.transmitService.Service.ActivationCodeService;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

@RestController
public class ActivationCodeController
{
    @Autowired
    private ActivationCodeService activationCodeService;

    @PostMapping(value = "/IndBank/transmit/generateActivationCode")
    public ResponseEntity<Map<String, Object>> generateActivationCode(@RequestBody ActivationCodeRequest activationCodeRequest)
    {
        String customerId = activationCodeRequest.getCustomerId();
        String phone = activationCodeRequest.getPhone();
        if(customerId==null || customerId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId cannot be null or empty!");
        }
        else if(phone==null || phone.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number cannot be null or empty!");
        }
        Map<String, Object> result = activationCodeService.addProfileInformation(customerId, phone);
        if(result!=null)
        {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/IndBank/transmit/verifyActivationCode")
    public ResponseEntity<Map<String, Object>> verifyActivationCode(@RequestHeader(name = "x-channel") String channel, @RequestHeader("x-group-member") String group_member, @RequestBody CodeAutenticateRequest activationCodeRequest)
    {
        if(channel==null || channel.isEmpty() || (!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE")))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(group_member==null || group_member.isEmpty() || (!group_member.equalsIgnoreCase("INDBANK")))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        String customerId = activationCodeRequest.getCallbacksValue1().getValue();
        String activationCode = activationCodeRequest.getCallbacksValue2().getValue();
        if(customerId==null || customerId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId cannot be null or empty!");
        }
        else if(activationCode==null || activationCode.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activation code cannot be null or empty!");
        }
        Boolean result = activationCodeService.authenticate(customerId, activationCode);
        if(result)
        {
            return new ResponseEntity<>(Map.of("authentication", true), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(Map.of("authentication", false), HttpStatus.UNAUTHORIZED);
        }
    }
}
