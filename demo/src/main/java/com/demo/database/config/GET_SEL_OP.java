package com.demo.database.config;


import com.demo.database.entities.Company;
import com.demo.database.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class GET_SEL_OP {
    private static GET_SEL_OP instance = null;

    public static GET_SEL_OP getInstance() {
        if (instance == null) {
            instance = new GET_SEL_OP();
        }
        return instance;
    }

    public String get(String cookie, OPTIONS options,CompanyService companyService) {
        try {
            String[] split = cookie.split("-");
            Company byId = companyService.findById(Long.parseLong(split[0]));
            switch (options) {
                case INTEGRATIONKEY:
                    return byId.getPrivateKey();
                case SECRETKEY:
                    return byId.getSecretKey();
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
        return "";
    }

    public enum OPTIONS {
        INTEGRATIONKEY,
        SECRETKEY
    }
}
