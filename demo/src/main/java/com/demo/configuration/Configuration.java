package com.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;


@org.springframework.context.annotation.Configuration
@ConfigurationProperties("app.api")
public class Configuration {
    private static Configuration instance = null;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Profile("dev")
    @Bean
    public String devTest() {
        System.out.println(login);
        return "development";
    }
}
