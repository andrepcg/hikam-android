package com.jwkj.entity;

public class Account {
    public String accessKey;
    public String countryCode;
    public String email;
    public String phone;
    public String rCode1;
    public String rCode2;
    public String sessionId;
    public String three_number;
    public String three_number2 = "000000";

    public Account(String three_number, String three_number2, String email, String phone, String sessionId, String code1, String code2, String countryCode) {
        this.three_number2 = three_number2;
        this.three_number = three_number;
        this.email = email;
        this.phone = phone;
        this.sessionId = sessionId;
        this.rCode1 = code1;
        this.rCode2 = code2;
        this.countryCode = countryCode;
    }
}
