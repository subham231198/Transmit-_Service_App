package com.example.indBank.transmitService.Service;


import com.example.indBank.transmitService.Entity.ActivationCode;
import com.example.indBank.transmitService.Utility.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ActivationCodeService {
    @Autowired
    private ActivationCodeRepo activationCodeRepo;

    public Map<String, Object> addProfileInformation(String customerId, String phone) {

        Instant now = Instant.now();

        ActivationCode code = activationCodeRepo
                .findByCustomerIdAndPhone(customerId, phone)
                .orElseGet(() -> {
                    ActivationCode newCode = new ActivationCode();
                    newCode.setCustomerId(customerId);
                    newCode.setPhone(phone);
                    return newCode;
                });

        boolean isExpired = code.getExpiresAt() != null &&
                now.isAfter(Instant.parse(code.getExpiresAt()));

        // Generate new code if missing or expired
        if (code.getActivationCode() == null || isExpired) {
            String activationCode = Generator.generateActivationCode();
            code.setActivationCode(activationCode);
            code.setIssuedAt(now.toString());
            code.setExpiresAt(now.plusSeconds(60).toString());
            code.setCode_used(false);
            activationCodeRepo.save(code);
        }

        return Map.of(
                "activationCode", code.getActivationCode()
        );
    }


    public Boolean authenticate(String customerId, String code)
    {
        Optional<ActivationCode> activationCode = activationCodeRepo.findByCustomerIdAndActivationCode(customerId, code);
        if(activationCode.isPresent())
        {
            ActivationCode existingCode = activationCode.get();
            Instant expiresAt = Instant.parse(existingCode.getExpiresAt());
            if(Instant.now().isBefore(expiresAt) && !existingCode.getCode_used())
            {
                existingCode.setCode_used(true);
                activationCodeRepo.save(existingCode);
                return true;
            }
        }
        return false;
    }
}
