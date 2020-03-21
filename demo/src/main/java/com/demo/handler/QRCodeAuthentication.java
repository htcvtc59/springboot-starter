package com.demo.handler;

import com.demo.model.QRCodeModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class QRCodeAuthentication {
    private static QRCodeAuthentication instance;

    public static QRCodeAuthentication getInstance() {
        if (instance == null) {
            instance = new QRCodeAuthentication();
        }
        return instance;
    }

    public QRCodeModel Auth(String path, String username, String otp, String challenge, String details,
                            String integrationKey,
                            String unixTimestamp, String hmac, String token) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("otp", otp);
            data.addProperty("challenge", challenge);
            data.addProperty("details", details);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            data.addProperty("authToken", token);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            QRCodeModel qrCodeModel = mapper.readValue(response.toString(), QRCodeModel.class);
            return qrCodeModel;
        } catch (Exception ex) {
        }
        return null;
    }

    public JsonObject StateCheck(String path, String username, String authMethod, String authToken, String integrationKey,
                                 String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("authMethod", authMethod);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("authToken", authToken);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            return response;
        } catch (Exception ex) {
        }
        return null;
    }
}
