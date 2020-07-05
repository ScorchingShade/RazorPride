package com.reddragon.razorsharp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
@ConfigurationProperties("razor.payment.sandbox")
public class PaymentModel {

    private String amount;
    private String currency;
    private String receipt;
    private String payment_capture;
    private String notes;
}
