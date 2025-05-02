package br.com.equatorial.genesys.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeConfig {

    @PostConstruct
    public void init() {
    	TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        System.setProperty("user.timezone", "America/Sao_Paulo");
    }
}
