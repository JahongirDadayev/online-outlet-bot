package com.example.onlineshopbot.service;

import com.example.onlineshopbot.model.result.ApiResponse;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsTextualMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Log4j2
public class OTPService {
    @Value(value = "${spring.otp.base-url}")
    private String baseUrl;

    @Value(value = "${spring.otp.api-key}")
    private String apiKey;

    @Value(value = "${spring.otp.sender}")
    private String sender;

    public ApiResponse sendSMS(String message, String recipient) {
        ApiClient client = initApiClient();
        SendSmsApi sendSmsApi = new SendSmsApi(client);
        SmsTextualMessage smsMessage = new SmsTextualMessage()
                .from(sender)
                .addDestinationsItem(new SmsDestination().to(recipient))
                .text(message);
        SmsAdvancedTextualRequest smsMessageRequest = new SmsAdvancedTextualRequest()
                .messages(Collections.singletonList(smsMessage));
        try {
            SmsResponse smsResponse = sendSmsApi.sendSmsMessage(smsMessageRequest);
            return new ApiResponse("SUCCESS", smsResponse, true);
        } catch (ApiException e) {
            e.printStackTrace();
            log.error(e);
            return new ApiResponse(e.getMessage(), null, true);
        }
    }

    private ApiClient initApiClient() {
        ApiClient client = new ApiClient();
        client.setApiKeyPrefix("App");
        client.setApiKey(apiKey);
        client.setBasePath(baseUrl);
        return client;
    }
}
