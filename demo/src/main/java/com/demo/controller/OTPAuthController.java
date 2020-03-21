package com.demo.controller;


import com.demo.database.config.GET_SEL_OP;
import com.demo.database.service.CompanyService;
import com.demo.handler.OTPAuthentication;
import com.demo.model.AuthModel;
import com.demo.model.OTPAuthModel;
import com.demo.response.DataResponse;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/otp")
public class OTPAuthController {
    @Autowired
    private CompanyService companyService;
    @Value("${app.otp.auth}")
    private String API_OTP;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private DataResponse OTPAuth(@RequestBody String body, HttpSession session,
                                 @CookieValue(value = "selectedOption",required = false) String selectedOption) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String ammount = data.get("amount").getAsString();
            String fromaccount = data.get("fromaccount").getAsString();
            String toaccount = data.get("toaccount").getAsString();
            String effdate = data.get("effdate").getAsString();
            String otp = data.get("otp").getAsString();
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + otp + ikey + unixTimeStamp;
            String hmac = Encryption.encrypt(skey, hmacData);

            OTPAuthModel auth = OTPAuthentication.getInstance().Auth(API_OTP, username, otp,
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
