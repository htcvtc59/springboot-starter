package com.demo.controller;


import com.demo.database.config.GET_SEL_OP;
import com.demo.database.service.CompanyService;
import com.demo.model.AuthModel;
import com.demo.model.LoginModel;
import com.demo.transport.HttpClientHelper;
import com.demo.util.Encryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(path = "/")
public class HomeController {

    @Autowired
    private CompanyService companyService;

    @Value("${app.api.login}")
    private String API_LOGIN;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    private ModelAndView home(HttpSession session) {
        ModelAndView mav = null;
        try {
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            if (authModel == null || authModel.getAuthToken().isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("loginModel", new LoginModel());
            } else {
                mav = new ModelAndView("home");
                mav.addObject("authModel", authModel);
            }
        } catch (Exception ex) {
            session.removeAttribute("auth");
            mav = new ModelAndView("login");
            mav.addObject("loginModel", new LoginModel());
            session.removeAttribute("auth");
            return mav;
        }
        return mav;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    private ModelAndView login(HttpSession session) {
        ModelAndView mav = null;
        try {
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            if (authModel == null || authModel.getAuthToken().isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("loginModel", new LoginModel());
            } else {
                mav = new ModelAndView("home");
            }
        } catch (Exception ex) {
            session.removeAttribute("auth");
            mav = new ModelAndView("login");
            mav.addObject("loginModel", new LoginModel());
            session.removeAttribute("auth");
            return mav;
        }
        return mav;
    }

    @RequestMapping(value = {"/logout"}, method = RequestMethod.GET)
    private ModelAndView logout(HttpSession session) {
        session.removeAttribute("auth");
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("loginModel", new LoginModel());
        return mav;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.POST)
    private ModelAndView login(@ModelAttribute LoginModel loginModel, HttpSession session,
                               @CookieValue(value = "selectedOption", required = false) String selectedOption) {
        ModelAndView mav = null;
        try {
            String password = loginModel.getPassword();
            String username = loginModel.getUsername();
            if (username.isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("errors", "Username not empty !");
                return mav;
            }
            if (password.isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("errors", "Password not empty !");
                return mav;
            }
            if (selectedOption == null || selectedOption.isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("errors", "Option not empty !");
                return mav;
            }

            String ikey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.INTEGRATIONKEY, companyService) : INTEGRATION_KEY;
            String skey = GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService).length() > 0 ?
                    GET_SEL_OP.getInstance().get(selectedOption, GET_SEL_OP.OPTIONS.SECRETKEY, companyService) : SECRET_KEY;

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + password + ikey + unixTimeStamp;
            String hmac = Encryption.encrypt(skey, hmacData);

            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("password", password);
            data.addProperty("integrationKey", ikey);
            data.addProperty("unixTimestamp", unixTimeStamp);
            data.addProperty("hmac", hmac);

            JsonObject result = HttpClientHelper.getInstance().sendTo(API_LOGIN, HttpClientHelper.METHODS.POST, data);
            if (result != null && !result.has("code")) {
                ObjectMapper mapper = new ObjectMapper();
                AuthModel authModel = mapper.readValue(result.toString(), AuthModel.class);
                session.setAttribute("auth", authModel);
                mav = new ModelAndView("home");
                mav.addObject("authModel", authModel);
                return mav;
            } else {
                mav = new ModelAndView("login");
                mav.addObject("errors", result.get("message").getAsString());
                return mav;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            session.removeAttribute("auth");
            mav = new ModelAndView("login");
            mav.addObject("loginModel", new LoginModel());
            session.removeAttribute("auth");
            return mav;
        }
    }

}
