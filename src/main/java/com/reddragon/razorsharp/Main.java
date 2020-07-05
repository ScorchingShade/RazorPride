package com.reddragon.razorsharp;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

/***
 * @author ankush
 * bootstrap vertx application
 */
@Slf4j
@SpringBootApplication
@EnableMongoRepositories
@ComponentScan
public class Main{

    private Vertx vertx;

    //Autowiring to create a bean of deployerVerticle
    @Autowired
    private DeployerVerticle deployerVerticle;

    //Using IOC to initialize vertx
    Main(){
        this.vertx=Vertx.vertx();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /***
     * Added postConstruct method to deploy all the verticles
     */
    @PostConstruct
    public void deployer(){
        vertx.deployVerticle(deployerVerticle, ar->{
            if(ar.succeeded()){
                System.out.println("--Vertx application running successfully, verticles deployed...");
            }
            else{
                System.out.println("!!Application startup failed...");
            }
        });
    }

}
