package com.example.indBank.transmitService.Service;

import com.example.indBank.transmitService.Entity.ActivationCode;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistentAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Optional;

@Repository
public interface ActivationCodeRepo extends JpaRepository<ActivationCode, Long>
{
    Optional<ActivationCode> findByCustomerId(String customerId);
    Optional<ActivationCode> findByPhone(String phone);
    Optional<ActivationCode> findByActivationCode(String activationCode);
    Optional<ActivationCode> findByCustomerIdAndPhone(String customerId, String phone);
    Optional<ActivationCode> findByCustomerIdAndActivationCode(String customerId, String activationCode);
}
