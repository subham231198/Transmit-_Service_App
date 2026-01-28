package com.example.indBank.transmitService.POJO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MAuth_DeprovisionRequest
{
    @JsonProperty(value = "tokenId")
    private String tokenId;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "mAuth_DeprovisionRequest{" +
                "tokenId='" + tokenId + '\'' +
                '}';
    }
}
