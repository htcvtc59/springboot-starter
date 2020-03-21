package com.demo.model;

public class RequestOtpChallengeModel {

    private String email;
    private String otpChallenge;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpChallenge() {
        return otpChallenge;
    }

    public void setOtpChallenge(String otpChallenge) {
        this.otpChallenge = otpChallenge;
    }
}
