package com.reddragon.razorsharp.routehandlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.reddragon.razorsharp.models.PaymentModel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class AppRouter extends AbstractVerticle {

    PaymentModel paymentModel;
    RazorpayClient razorpayClient;

    public AppRouter(PaymentModel paymentModel) {
        this.paymentModel = paymentModel;
    }


    ResourceBundle bundle = ResourceBundle.getBundle("application");
    int port = Integer.parseInt(bundle.getString("vertx.port"));

    @Override
    public void start() throws Exception {
        super.start();


        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);


        Set<String> allowedHeaders = new HashSet<>();

        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        /*
         * these methods aren't necessary for this sample,
         * but you may need them for your projects
         */
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));


        //Handler for Payment ops
        router.post("/pay").handler(BodyHandler.create());
        router.post("/pay").handler(this::paymentHandler);

        //Handler for recheck ops
        router.post("/response").handler(BodyHandler.create());
        router.post("/response").handler(this::responseHandler);

        server.requestHandler(router).listen(port);


    }


    private void responseHandler(RoutingContext routingContext) {

        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.putHeader("content-type", "text/plain");

        HttpServerRequest request = routingContext.request();


        JsonElement jsonElement = new JsonParser().parse(routingContext.getBodyAsString());

        JsonObject fetchedDocument = jsonElement.getAsJsonObject();

        System.out.println(fetchedDocument.toString());

        JSONObject sentResponse=new JSONObject();

        try{
            razorpayClient = new RazorpayClient("rzp_test_0VoyRtTV4KRbJ0", "abdpfoUY5wbko2tFo48jDqVs");
            JSONObject captureRequest = new JSONObject();

            //Guard clause to check null;
            if(fetchedDocument.get("razorpay_payment_id")==null)
                throw new NullPointerException();

            //Important note, razorpay payment capture amount must always be in paise not rupees
            String amount = fetchedDocument.get("amount").toString()+"00";
            String paymentId=fetchedDocument.get("razorpay_payment_id").toString().replaceAll("(\")","");

            captureRequest.put("amount", amount);
            captureRequest.put("currency", "INR");


            Payment payment = razorpayClient.Payments.capture(paymentId, captureRequest);

        }
        catch (RazorpayException e){
            sentResponse.put("response", "Could not store into db, please store manually by logging on to your mongo admin console.");
            sentResponse.put("status","401");
            response.end(sentResponse.toString());
            System.out.println(e.getMessage());
        }
        catch ( NullPointerException eu){
            sentResponse.put("response","Could not Fetch Payment Id");
            sentResponse.put("status","402");
            response.end(sentResponse.toString());

        }

        sentResponse.put("response","Success");
        sentResponse.put("status","202");
        response.end(sentResponse.toString());

    }

    private void paymentHandler(RoutingContext routingContext) {

        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.putHeader("content-type", "text/plain");

        JsonElement jsonElement = new JsonParser().parse(routingContext.getBodyAsString());

        JsonObject fetchedDocument = jsonElement.getAsJsonObject();

        System.out.println(fetchedDocument.toString());

        try {
            razorpayClient = new RazorpayClient("rzp_test_0VoyRtTV4KRbJ0", "abdpfoUY5wbko2tFo48jDqVs");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Integer.parseInt(fetchedDocument.get("amount").toString().replaceAll("[\"]",""))); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_15");
            orderRequest.put("payment_capture", true);

            Order order = razorpayClient.Orders.create(orderRequest);
            System.out.println("this is order"+order);
            response.end(order.toString());
        } catch (RazorpayException e) {
            // Handle Exception
            response.end("no response");
            System.out.println(e.getMessage());
        }


       // Gson gson = new Gson();
      //  String json = gson.toJson(map).replaceAll("[\\\\][\\\"][\\\"]","\"").replaceAll("[\\\"][\\\\][\\\"]","\"").replaceAll("(\\\\u003d)","=").trim();

       // System.out.println(json);

        //response.end(json);


    }

}
