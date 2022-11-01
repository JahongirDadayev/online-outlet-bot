package com.example.onlineshopbot.controller;

import com.example.onlineshopbot.entity.DbUser;
import com.example.onlineshopbot.enums.BotState;
import com.example.onlineshopbot.service.BotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@Log4j2
public class BotController extends TelegramLongPollingBot {
    @Autowired
    private BotService botService;

    @Value(value = "${spring.telegram.bot.username}")
    private String username;

    @Value(value = "${spring.telegram.bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            botService.typing(update);
            DbUser user = botService.getUser(update);
            if (user.getState().equals(BotState.START)) {
                botService.tgStart(update, user, true);
            } else if (user.getState().equals(BotState.CHOOSE_LANGUAGE)) {
                botService.tgChooseLanguage(update, user, true);
            } else if (user.getState().equals(BotState.GET_PHONE_NUMBER)) {
                botService.tgGetPhoneNumber(update, user, true);
            } else if (user.getState().equals(BotState.VERIFICATION)) {
                botService.tgVerification(update, user, true);
            } else if (user.getState().equals(BotState.GET_FIRST_NAME)) {
                botService.tgGetFirstName(update, user, true);
            } else if (user.getState().equals(BotState.GET_LAST_NAME)) {
                botService.tgGetLastName(update, user, true);
            } else if (user.getState().equals(BotState.MENU)) {
                botService.tgMenu(update, user, true);
            } else if (user.getState().equals(BotState.PROFILE)) {
                botService.tgProfile(update, user, true);
            } else {
                botService.conflict(update, user.getLanguage());
            }
        } catch (Exception e) {
            log.error("CONFLICT {}", e.getMessage());
            e.printStackTrace();
            botService.conflict(update, null);
        }
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }
}