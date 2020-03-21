package com.demo.transport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class HttpClientHelper {
    private static HttpClientHelper instance = null;

    public static HttpClientHelper getInstance() {
        if (instance == null) {
            instance = new HttpClientHelper();
        }
        return instance;
    }

    public enum METHODS {
        GET, POST, PUT, DELETE
    }

    public JsonObject sendTo(String path, METHODS method, JsonObject data) {
        JsonObject res = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            StringBuffer result = new StringBuffer();
            String line = "";
            HttpResponse response;
            BufferedReader rd;
            switch (method) {
                case GET:
                    HttpGet httpGET = new HttpGet(path);
                    httpGET.setHeader("Accept", "application/json");
                    httpGET.setHeader("Content-type", "application/json");
                    response = client.execute(httpGET);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        res = new JsonParser().parse(result.toString()).getAsJsonObject();
                    }
                    break;
                case PUT:
                    HttpPut httpPUT = new HttpPut(path);
                    httpPUT.setHeader("Accept", "application/json");
                    httpPUT.setHeader("Content-type", "application/json");
                    response = client.execute(httpPUT);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        res = new JsonParser().parse(result.toString()).getAsJsonObject();
                    }
                    break;
                case POST:
                    HttpPost httpPOST = new HttpPost(path);
                    httpPOST.setHeader("Accept", "application/json");
                    httpPOST.setHeader("Content-type", "application/json");

                    httpPOST.setEntity(new StringEntity(data.toString(), "UTF-8"));
                    response = client.execute(httpPOST);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        res = new Gson().fromJson(result.toString(), JsonObject.class);
                        String message = res.get("message").getAsString();
                        int code = res.get("code").getAsInt();
                        if (message.equals("Success") && code == 0) {
                            res = new Gson().fromJson(res.get("object").getAsString(), JsonObject.class);
                        }
                    }
                    break;
                case DELETE:
                    HttpDelete httpDELETE = new HttpDelete(path);
                    httpDELETE.setHeader("Accept", "application/json");
                    httpDELETE.setHeader("Content-type", "application/json");
                    response = client.execute(httpDELETE);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        res = new JsonParser().parse(result.toString()).getAsJsonObject();
                    }
                    break;
            }
            return new JsonParser().parse(res.toString()).getAsJsonObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


}
