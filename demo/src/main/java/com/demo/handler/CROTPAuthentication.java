package com.demo.handler;

import com.demo.model.CROTPAuthModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class CROTPAuthentication {

    private static CROTPAuthentication instance;

    public static CROTPAuthentication getInstance() {
        if (instance == null) {
            instance = new CROTPAuthentication();
        }
        return instance;
    }

    public CROTPAuthModel Auth(String path, String username, String challenge, String crOtp, String integrationKey,
                               String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("challenge", challenge);
            data.addProperty("crOtp", crOtp);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            CROTPAuthModel crotpAuthModel = mapper.readValue(response.toString(), CROTPAuthModel.class);
            return crotpAuthModel;
        } catch (Exception ex) {
        }
        return null;
    }

}
