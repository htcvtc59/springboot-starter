package com.demo.controller;

import com.demo.database.config.GET_SEL_OP;
import com.demo.database.service.CompanyService;
import com.demo.handler.CROTPAuthentication;
import com.demo.handler.RequestOtpChallenge;
import com.demo.model.AuthModel;
import com.demo.model.CROTPAuthModel;
import com.demo.model.RequestOtpChallengeModel;
import com.demo.response.DataResponse;
import com.demo.response.ResponseCode;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/crotp")
public class CROTPAuthController {
    @Autowired
    private CompanyService companyService;

    @Value("${app.authCrOtp.auth}")
    private String API_AUTHCROTP;
    @Value("${app.requestOtpChallenge}")
    private String API_REQUEST_AUTHCROTP;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/request"}, method = RequestMethod.POST)
    private DataResponse Request(HttpSession session, @CookieValue(value = "selectedOption", required = false) String selectedOption) {
        try {
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + ikey + unixTimeStamp;
            String hmac = Encryption.encrypt(skey, hmacData);

            RequestOtpChallengeModel request = RequestOtpChallenge.getInstance().request(API_REQUEST_AUTHCROTP, username, ikey, unixTimeStamp, hmac);

            if (request != null) {
                String challenge = request.getOtpChallenge();
                return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", challenge);
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private DataResponse CROTPAuth(@RequestBody String body, HttpSession session,
                                   @CookieValue(value = "selectedOption", required = false) String selectedOption) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String crotp = data.get("crotp").getAsString();
            String challenge = data.get("challenge").getAsString();

            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + crotp + challenge + ikey + unixTimeStamp;
            String hmac = Encryption.encrypt(skey, hmacData);

            CROTPAuthModel auth = CROTPAuthentication.getInstance().Auth(API_AUTHCROTP, username, challenge, crotp,
                    ikey, unixTimeStamp, hmac);
            if (auth != null) {
                return DataResponse.SUCCESSFUL;
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }

}
