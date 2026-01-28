package com.example.indBank.transmitService.Exceptions;

public class CustomerNotFoundException extends RuntimeException
{
    public CustomerNotFoundException(String message)
    {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable clause)
    {
        super(message, clause);
    }
}
