package com.example.indBank.transmitService.POJO;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeAutenticateRequest
{
    @JsonProperty(value = "callbacksInput1")
    private CallbacksInput callbacksInput1;

    @JsonProperty(value = "callbacksValue1")
    private CallbacksValue callbacksValue1;

    @JsonProperty(value = "callbacksInput2")
    private CallbacksInput callbacksInput2;

    @JsonProperty(value = "callbacksValue2")
    private CallbacksValue callbacksValue2;

    public CallbacksInput getCallbacksInput1() {
        return callbacksInput1;
    }

    public void setCallbacksInput1(CallbacksInput callbacksInput1) {
        this.callbacksInput1 = callbacksInput1;
    }

    public CallbacksValue getCallbacksValue1() {
        return callbacksValue1;
    }

    public void setCallbacksValue1(CallbacksValue callbacksValue1) {
        this.callbacksValue1 = callbacksValue1;
    }

    public CallbacksInput getCallbacksInput2() {
        return callbacksInput2;
    }

    public void setCallbacksInput2(CallbacksInput callbacksInput2) {
        this.callbacksInput2 = callbacksInput2;
    }

    public CallbacksValue getCallbacksValue2() {
        return callbacksValue2;
    }

    public void setCallbacksValue2(CallbacksValue callbacksValue2) {
        this.callbacksValue2 = callbacksValue2;
    }

    @Override
    public String toString() {
        return "CodeAutenticateRequest{" +
                "callbacksInput1=" + callbacksInput1 +
                ", callbacksValue1=" + callbacksValue1 +
                ", callbacksInput2=" + callbacksInput2 +
                ", callbacksValue2=" + callbacksValue2 +
                '}';
    }
}
