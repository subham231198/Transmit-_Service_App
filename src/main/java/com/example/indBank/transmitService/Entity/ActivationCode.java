package com.example.indBank.transmitService.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "activation_code")
public class ActivationCode
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @JsonProperty(value = "activation_code")
    @Column(name = "activation_code")
    private String activationCode;

    @JsonProperty(value = "customerId")
    @Column(name = "customerId", unique = true)
    private String customerId;

    @JsonProperty(value = "phone")
    @Column(name = "phone", unique = true)
    private String phone;

    @JsonProperty(value = "issued_at")
    @Column(name = "issued_at")
    private String issuedAt;

    @JsonProperty(value = "expires_at")
    @Column(name = "expires_at")
    private String expiresAt;

    @JsonProperty(value = "code_used")
    @Column(name = "code_used")
    private Boolean code_used;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Boolean getCode_used() {
        return code_used;
    }

    public void setCode_used(Boolean code_used) {
        this.code_used = code_used;
    }

    @Override
    public String toString() {
        return "ActivationCode{" +
                "user_id=" + user_id +
                ", activationCode='" + activationCode + '\'' +
                ", customerId='" + customerId + '\'' +
                ", phone='" + phone + '\'' +
                ", issuedAt='" + issuedAt + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", code_used=" + code_used +
                '}';
    }
}
