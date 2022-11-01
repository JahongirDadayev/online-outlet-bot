package com.example.onlineshopbot.service;

import com.example.onlineshopbot.model.EmailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String username;

    public void postEmail(EmailModel emailModel) throws MessagingException, IOException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username, "noreply@gmail.com");
        helper.setTo(emailModel.getEmail());
        helper.setSubject(emailModel.getSubject());
        helper.setText(getContentFromTemplate(emailModel.getCode(), emailModel.getSubject(), emailModel.getMessage()), true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> javaMailSender.send(mimeMessage));
    }

    private String getContentFromTemplate(String code, String subject, String message) throws IOException {
        return Files.readString(Paths.get("src/main/resources/email-templates/activate_account.html")).replace("CODE", code).replace("SUBJECT", subject).replace("MESSAGE", message);
    }
}