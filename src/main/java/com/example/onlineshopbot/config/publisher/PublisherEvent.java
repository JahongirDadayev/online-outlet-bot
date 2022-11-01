package com.example.onlineshopbot.config.publisher;

import com.example.onlineshopbot.model.EmailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherEvent {
    private final ApplicationEventPublisher publisher;

    public void setPublisher(EmailModel emailModel){
        publisher.publishEvent(emailModel);
    }
}