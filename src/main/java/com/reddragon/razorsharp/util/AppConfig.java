package com.reddragon.razorsharp.util;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

    @Autowired
    private Environment environment;

    public Vertx vertx= Vertx.vertx();

    public String applicationName(){
        return environment.getProperty("spring.application.name");
    }

    public int httpPort() {
        return environment.getProperty("server.port", Integer.class);

    }

}
