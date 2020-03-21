package com.demo.handler;

import com.demo.model.OTPAuthModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class OTPAuthentication {
    private static OTPAuthentication instance;

    public static OTPAuthentication getInstance() {
        if (instance == null) {
            instance = new OTPAuthentication();
        }
        return instance;
    }

    public OTPAuthModel Auth(String path, String username, String otp, String integrationKey,
                             String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("otp", otp);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            OTPAuthModel otpAuthModel = mapper.readValue(response.toString(), OTPAuthModel.class);
            return otpAuthModel;
        } catch (Exception ex) {
        }
        return null;
    }


}
