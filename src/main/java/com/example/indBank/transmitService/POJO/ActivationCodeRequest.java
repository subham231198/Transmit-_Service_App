package com.example.indBank.transmitService.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivationCodeRequest
{
    @JsonProperty(value = "customerId")
    @NonNull
    private String customerId;

    @JsonProperty(value = "phone")
    @NonNull
    private String phone;

    public @NonNull String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NonNull String customerId) {
        this.customerId = customerId;
    }

    public @NonNull String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ActivationCodeRequest{" +
                "customerId='" + customerId + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
