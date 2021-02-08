package com.example.gatewayapp.Helpers;

public class PinGenerator {
    public static String generate()
    {
        int randomPIN = (int) ( Math.random() * 999998) + 100000;
        return String.valueOf(randomPIN);
    }
}
