package com.example.onlineshopbot.config.publisher;

import com.example.onlineshopbot.model.EmailModel;
import com.example.onlineshopbot.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PublisherListener {
    private final EmailService emailService;

    @EventListener
    @Async
    public void listener(EmailModel emailModel) throws MessagingException, IOException {
        emailService.postEmail(emailModel);
    }
}