package com.example.indBank.transmitService.Service;

import com.example.indBank.transmitService.Entity.mAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface mAuthRepo extends JpaRepository<mAuthEntity, Long> {
    Optional<mAuthEntity> findByCustomerId(String customerId);

    Optional<mAuthEntity> findByDeviceId(String deviceId);

    Optional<mAuthEntity> findByCustomerIdAndDeviceId(String customerId, String deviceId);

    Optional<mAuthEntity> deleteByCustomerId(String customerId);
}
