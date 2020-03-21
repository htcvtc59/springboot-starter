package com.demo.controller;

import com.demo.database.config.GET_SEL_OP;
import com.demo.database.service.CompanyService;
import com.demo.handler.QRCodeAuthentication;
import com.demo.handler.RequestQRCode;
import com.demo.model.AuthModel;
import com.demo.model.QRCodeModel;
import com.demo.model.RequestQrCodeModel;
import com.demo.response.DataResponse;
import com.demo.response.ResponseCode;
import com.demo.util.Encryption;
import com.demo.util.QRCodeGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Base64;


@RestController
@RequestMapping(value = "/qr")
public class QRCodeAuthController {
    @Autowired
    private CompanyService companyService;
    @Value("${app.qr.auth}")
    private String API_QR_CODE;
    @Value("${app.requestQrCode}")
    private String API_REQUEST_QR;
    @Value("${app.statecheck}")
    private String API_STATECHECK;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/request"}, method = RequestMethod.POST)
    private DataResponse Request(@RequestBody String body, HttpSession session,
                                 @CookieValue(value = "selectedOption",required = false) String selectedOption) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String ammount = data.get("amount").getAsString();
            String fromaccount = data.get("fromaccount").getAsString();
            String toaccount = data.get("toaccount").getAsString();
            String effdate = data.get("effdate").getAsString();
            String detail = "Transaction from " + fromaccount + " to " + toaccount
                    + "\nAmount : " + ammount
                    + "\nEffective Date :" + effdate;
            detail = Base64.getEncoder().encodeToString(detail.getBytes());

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacDataRequest = username + detail + ikey + unixTimeStamp;
            String hmacReq = Encryption.encrypt(skey, hmacDataRequest);
            RequestQrCodeModel request = RequestQRCode.getInstance().request(API_REQUEST_QR,
                    username, detail,
                    ikey, unixTimeStamp, hmacReq);
            String authToken = request.getAuthToken();

            if (request != null) {
                String challenge = request.getOtpChallenge();
                String qrCode = request.getQrCode();
                String plainText = request.getPlainText();
                QRCodeGenerator.generateQRCodeImage(qrCode, 300, 300, QRCodeGenerator.QR_CODE_IMAGE_PATH);
                String path = "/images/QRCode.png";
                byte[] qrCodeImageByteArray = QRCodeGenerator.getQRCodeImageByteArray(qrCode, 300, 300);
                return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", qrCode + "||" + challenge + "||" + path + "||" + qrCodeImageByteArray + "||" + authToken + "||" + detail + "||" + plainText);
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private DataResponse QRAuth(@RequestBody String body, HttpSession session,
                                @CookieValue(value = "selectedOption",required = false) String selectedOption) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String detail = data.get("detail").getAsString();
            String token = data.get("token").getAsString();
            String challenge = data.get("challenge").getAsString();
            String qrcode = data.get("qrcode").getAsString();
            String qrotp = data.get("qrotp").getAsString();
            String qrplaintext = data.get("qrplaintext").getAsString();
            String details = Base64.getEncoder().encodeToString(qrplaintext.getBytes());

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + qrotp + challenge + details + ikey + unixTimeStamp + token;
            String hmac = Encryption.encrypt(skey, hmacData);

            QRCodeModel auth = QRCodeAuthentication.getInstance().Auth(API_QR_CODE, username, qrotp,
                    challenge, details, ikey, unixTimeStamp, hmac, token);

            return DataResponse.FAILED;
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }


    @RequestMapping(value = {"/statecheck"}, method = RequestMethod.POST)
    private DataResponse StateCheck(@RequestBody String body, HttpSession session,
                                    @CookieValue(value = "selectedOption",required = false) String selectedOption) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String authToken = data.get("authToken").getAsString();
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String authMethod = "QRCODE";

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + authMethod + ikey + unixTimeStamp + authToken;
            String hmac = Encryption.encrypt(skey, hmacData);

            JsonObject res = QRCodeAuthentication.getInstance().StateCheck(API_STATECHECK, username,
                    authMethod, authToken, ikey, unixTimeStamp, hmac);

            if (res != null && res.has("code") && res.get("code").getAsString().equals("23001")) {
                return DataResponse.FAILED;
            } else if (res != null && res.has("appId") && res.get("appId").getAsString().length() > 0) {
                return DataResponse.SUCCESSFUL;
            }
            return new DataResponse(ResponseCode.CANCELLED, ResponseCode.CANCELLED, null);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return new DataResponse(ResponseCode.CANCELLED, ResponseCode.CANCELLED, null);
    }


}

