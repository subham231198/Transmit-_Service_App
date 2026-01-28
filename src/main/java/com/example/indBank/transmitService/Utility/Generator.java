package com.example.indBank.transmitService.Utility;

public class Generator
{
    public static String generateActivationCode()
    {
        int length = 6;
        StringBuilder activationCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10);
            activationCode.append(digit);
        }
        return activationCode.toString();
    }

    public static String generateFraudSessionId()
    {
        int code = 1000000 + (int)(Math.random() * 9000000);
        Integer sessionId = Integer.valueOf(code);
        return sessionId.toString();
    }
}
