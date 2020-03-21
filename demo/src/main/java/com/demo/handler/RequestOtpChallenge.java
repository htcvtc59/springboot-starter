package com.demo.handler;

import com.demo.model.RequestOtpChallengeModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class RequestOtpChallenge {

    private static RequestOtpChallenge instance;

    public static RequestOtpChallenge getInstance() {
        if (instance == null) {
            instance = new RequestOtpChallenge();
        }
        return instance;
    }

    public RequestOtpChallengeModel request(String path, String username, String integrationKey,
                                            String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            RequestOtpChallengeModel requestOtpChallengeModel = mapper.readValue(response.toString(), RequestOtpChallengeModel.class);
            return requestOtpChallengeModel;
        } catch (Exception ex) {
        }
        return null;
    }
}
