package com.reddragon.razorsharp;

import com.reddragon.razorsharp.models.PaymentModel;
import com.reddragon.razorsharp.routehandlers.AppRouter;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeployerVerticle extends AbstractVerticle {

    @Autowired
    public PaymentModel paymentModel;

    @Override
    public void start() throws Exception {
        super.start();

        vertx.deployVerticle(new AppRouter(paymentModel), ar->{
            if(ar.succeeded()){
                System.out.println("--Routing to different channels");
            }
            else{
                System.out.println("--Routing to different channels failed");
            }
        });
    }
}
