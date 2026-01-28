package com.example.indBank.transmitService.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mAuthEntity")
public class mAuthEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(value = "deviceId")
    @Column(name = "deviceId")
    private String deviceId;

    @JsonProperty(value = "customerId")
    @Column(name = "customerId")
    private String customerId;

    @JsonProperty(value = "is_profile_provisioned")
    @Column(name = "is_profile_provisioned")
    private Boolean is_profile_provisioned;

    @JsonProperty(value = "profile_provisioned_timeStamp")
    @Column(name = "profile_provisioned_timeStamp")
    private String profile_provisioned_timeStamp;

    @JsonProperty(value = "mPIN")
    @Column(name = "mPIN")
    private String mPIN;

    @JsonProperty(value = "mPIN_issued_timeStamp")
    @Column(name = "mPIN_issued_timeStamp")
    private String mPIN_issued_timeStamp;

    @JsonProperty(value = "mPIN_expiry_timeStamp")
    @Column(name = "mPIN_expiry_timeStamp")
    private String mPIN_expiry_timeStamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getIs_profile_provisioned() {
        return is_profile_provisioned;
    }

    public void setIs_profile_provisioned(Boolean is_profile_provisioned) {
        this.is_profile_provisioned = is_profile_provisioned;
    }

    public String getProfile_provisioned_timeStamp() {
        return profile_provisioned_timeStamp;
    }

    public void setProfile_provisioned_timeStamp(String profile_provisioned_timeStamp) {
        this.profile_provisioned_timeStamp = profile_provisioned_timeStamp;
    }

    public String getmPIN() {
        return mPIN;
    }

    public void setmPIN(String mPIN) {
        this.mPIN = mPIN;
    }

    public String getmPIN_issued_timeStamp() {
        return mPIN_issued_timeStamp;
    }

    public void setmPIN_issued_timeStamp(String mPIN_issued_timeStamp) {
        this.mPIN_issued_timeStamp = mPIN_issued_timeStamp;
    }

    public String getmPIN_expiry_timeStamp() {
        return mPIN_expiry_timeStamp;
    }

    public void setmPIN_expiry_timeStamp(String mPIN_expiry_timeStamp) {
        this.mPIN_expiry_timeStamp = mPIN_expiry_timeStamp;
    }

    @Override
    public String toString() {
        return "mAuthEntity{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", is_profile_provisioned=" + is_profile_provisioned +
                ", profile_provisioned_timeStamp='" + profile_provisioned_timeStamp + '\'' +
                ", mPIN='" + mPIN + '\'' +
                ", mPIN_issued_timeStamp='" + mPIN_issued_timeStamp + '\'' +
                ", mPIN_expiry_timeStamp='" + mPIN_expiry_timeStamp + '\'' +
                '}';
    }
}
