package com.demo.handler;

import com.demo.model.RequestQrCodeModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class RequestQRCode {
    private static RequestQRCode instance;

    public static RequestQRCode getInstance() {
        if (instance == null) {
            instance = new RequestQRCode();
        }
        return instance;
    }

    public RequestQrCodeModel request(String path, String username, String details, String integrationKey,
                                      String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("details", details);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            RequestQrCodeModel requestQrCodeModel = mapper.readValue(response.toString(), RequestQrCodeModel.class);
            return requestQrCodeModel;
        } catch (Exception ex) {
        }
        return null;
    }


}
