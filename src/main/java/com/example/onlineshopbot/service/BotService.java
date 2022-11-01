package com.example.onlineshopbot.service;

import com.example.onlineshopbot.controller.BotController;
import com.example.onlineshopbot.entity.DbUser;
import com.example.onlineshopbot.enums.BotState;
import com.example.onlineshopbot.model.result.ApiResponse;
import com.example.onlineshopbot.repository.UserRepository;
import com.example.onlineshopbot.utils.GenerationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class BotService {
    private final BotController botController;

    private final UserRepository userRepository;

    private final LocalizeService localizeService;

    private final OTPService otpService;

    private final GenerationCode generationCode;

    @Value(value = "${spring.person.profile.default-picture}")
    private String personProfileDefaultPicture;

    public DbUser getUser(Update update) {
        Message message = getMessage(update);
        Optional<DbUser> optionalDbUser = userRepository.findByChatId(message.getChatId().toString());
        if (optionalDbUser.isPresent()) {
            if (message.hasText() && message.getText().equals("/start")) {
                DbUser user = optionalDbUser.get();
                if (!user.getEnable()) {
                    user.setState(BotState.START);
                } else {
                    user.setState(BotState.MENU);
                }
                userRepository.save(user);
                return user;
            } else {
                return optionalDbUser.get();
            }
        } else {
            DbUser user = new DbUser();
            user.setChatId(message.getChatId().toString());
            user.setFirstName(message.getFrom().getFirstName());
            user.setState(BotState.START);
            userRepository.save(user);
            return user;
        }
    }

    public void tgStart(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = update.getMessage();
        if (toCheck) {
            if (message.hasText() && message.getText().equals("/start")) {
                InlineKeyboardButton inlineButton1 = new InlineKeyboardButton();
                inlineButton1.setText("\uD83C\uDDFA\uD83C\uDDFF uz");
                inlineButton1.setCallbackData("uz");
                InlineKeyboardButton inlineButton2 = new InlineKeyboardButton();
                inlineButton2.setText("\uD83C\uDDF7\uD83C\uDDFA ru");
                inlineButton2.setCallbackData("ru");
                InlineKeyboardButton inlineButton3 = new InlineKeyboardButton();
                inlineButton3.setText("\uD83C\uDDEC\uD83C\uDDE7 eng");
                inlineButton3.setCallbackData("eng");
                InlineKeyboardMarkup inlineMarkup = createInlineMarkup(Collections.singletonList(Arrays.asList(inlineButton1, inlineButton2, inlineButton3)));
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setReplyMarkup(inlineMarkup);
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setText("➖ Til tanlang \uD83C\uDDFA\uD83C\uDDFF\n➖ Выберите язык \uD83C\uDDF7\uD83C\uDDFA\n➖ Choose a language \uD83C\uDDEC\uD83C\uDDE7");
                botController.execute(sendMessage);
                user.setState(BotState.CHOOSE_LANGUAGE);
                userRepository.save(user);
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }

    public void tgChooseLanguage(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                if (data.equals("uz") || data.equals("ru") || data.equals("eng")) {
                    user.setLanguage(data);
                    userRepository.save(user);
                    KeyboardButton button = new KeyboardButton();
                    button.setRequestContact(true);
                    button.setText(localizeService.translateBot("message.bot.button.choose-language.button", user.getLanguage()));
                    ReplyKeyboardMarkup markup = createMarkup(Collections.singletonList(Collections.singletonList(button)));
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(markup);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText(localizeService.translateBot("message.bot.choose-language.get-phone-number", user.getLanguage()).replace("FIRST_NAME", user.getFirstName()));
                    botController.execute(sendMessage);
                    user.setState(BotState.GET_PHONE_NUMBER);
                    userRepository.save(user);
                } else {
                    conflict(update, user.getLanguage());
                }
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }

    public void tgGetPhoneNumber(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasContact()) {
                user.setPhoneNumber(message.getContact().getPhoneNumber().contains("+") ? message.getContact().getPhoneNumber() : "+" + message.getContact().getPhoneNumber());
                user.setCode(generationCode.generationCode());
                userRepository.save(user);
                ApiResponse apiResponse = otpService.sendSMS(localizeService.translateBot("message.bot.get-phone-number.send-sms", user.getLanguage()) + user.getCode(), message.getContact().getPhoneNumber().replace("+", ""));
                if (apiResponse.getSuccess()) {
                    ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setReplyMarkup(keyboardRemove);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText(localizeService.translateBot("message.bot.get-phone-number.send-sms.success", user.getLanguage()));
                    botController.execute(sendMessage);
                    user.setState(BotState.VERIFICATION);
                    userRepository.save(user);
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText(localizeService.translateBot("message.error.bot.replace", user.getLanguage()));
                    botController.execute(sendMessage);
                }
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }

    public void tgVerification(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasText()) {
                if ((!Objects.isNull(user.getCode()) && user.getCode().equals(message.getText()))) {
                    user.setCode(null);
                    userRepository.save(user);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText(localizeService.translateBot("message.bot.verification.get-firstname", user.getLanguage()));
                    botController.execute(sendMessage);
                    user.setState(BotState.GET_FIRST_NAME);
                    userRepository.save(user);
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText(localizeService.translateBot("message.error.bot.wrong-code", user.getLanguage()));
                    botController.execute(sendMessage);
                }
            } else {
                conflict(update, user.getLanguage());
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setText(localizeService.translateBot("message.bot.verification.get-firstname", user.getLanguage()));
            botController.execute(sendMessage);
            user.setState(BotState.GET_FIRST_NAME);
            userRepository.save(user);
        }
    }

    public void tgGetFirstName(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasText()) {
                user.setFirstName(message.getText());
                userRepository.save(user);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setText(localizeService.translateBot("message.bot.get-first-name.get-last-name", user.getLanguage()));
                botController.execute(sendMessage);
                user.setState(BotState.GET_LAST_NAME);
                userRepository.save(user);
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }

    public void tgGetLastName(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasText()) {
                List<List<PhotoSize>> photos = botController.execute(new GetUserProfilePhotos(message.getChatId(), 0, 100)).getPhotos();
                user.setFieldId(photos.isEmpty() ? personProfileDefaultPicture : photos.get(0).get(0).getFileId());
                user.setLastName(message.getText());
                user.setEnable(true);
                userRepository.save(user);
                KeyboardButton button1 = new KeyboardButton();
                button1.setText(localizeService.translateBot("message.bot.get-last-name.button1", user.getLanguage()));
                KeyboardButton button2 = new KeyboardButton();
                button2.setText(localizeService.translateBot("message.bot.get-last-name.button2", user.getLanguage()));
                KeyboardButton button3 = new KeyboardButton();
                button3.setText(localizeService.translateBot("message.bot.get-last-name.button3", user.getLanguage()));
                KeyboardButton button4 = new KeyboardButton();
                button4.setText(localizeService.translateBot("message.bot.get-last-name.button4", user.getLanguage()));
                ReplyKeyboardMarkup markup = createMarkup(Arrays.asList(Collections.singletonList(button1), Collections.singletonList(button2), Collections.singletonList(button3), Collections.singletonList(button4)));
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(message.getChatId());
                sendMessage.setReplyMarkup(markup);
                sendMessage.setParseMode(ParseMode.MARKDOWN);
                sendMessage.setText(localizeService.translateBot("message.bot.get-last-name.confirmed", user.getLanguage()));
                botController.execute(sendMessage);
                user.setState(BotState.MENU);
                userRepository.save(user);
            } else {
                conflict(update, user.getLanguage());
            }
        } else {
            KeyboardButton button1 = new KeyboardButton();
            button1.setText(localizeService.translateBot("message.bot.get-last-name.button1", user.getLanguage()));
            KeyboardButton button2 = new KeyboardButton();
            button2.setText(localizeService.translateBot("message.bot.get-last-name.button2", user.getLanguage()));
            KeyboardButton button3 = new KeyboardButton();
            button3.setText(localizeService.translateBot("message.bot.get-last-name.button3", user.getLanguage()));
            KeyboardButton button4 = new KeyboardButton();
            button4.setText(localizeService.translateBot("message.bot.get-last-name.button4", user.getLanguage()));
            ReplyKeyboardMarkup markup = createMarkup(Arrays.asList(Collections.singletonList(button1), Collections.singletonList(button2), Collections.singletonList(button3), Collections.singletonList(button4)));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyMarkup(markup);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setText(localizeService.translateBot("message.bot.choice", user.getLanguage()));
            botController.execute(sendMessage);
            user.setState(BotState.MENU);
            userRepository.save(user);
        }
    }

    public void tgMenu(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasText()) {
                if (message.getText().equals(localizeService.translateBot("message.bot.get-last-name.button1", user.getLanguage()))) {

                } else if (message.getText().equals(localizeService.translateBot("message.bot.get-last-name.button2", user.getLanguage()))) {

                } else if (message.getText().equals(localizeService.translateBot("message.bot.get-last-name.button3", user.getLanguage()))) {

                } else if (message.getText().equals(localizeService.translateBot("message.bot.get-last-name.button4", user.getLanguage()))) {
                    KeyboardButton button1 = new KeyboardButton();
                    button1.setText(localizeService.translateBot("message.bot.menu.button1", user.getLanguage()));
                    KeyboardButton button2 = new KeyboardButton();
                    button2.setText(localizeService.translateBot("message.bot.menu.button2", user.getLanguage()));
                    ReplyKeyboardMarkup markup = createMarkup(Arrays.asList(Collections.singletonList(button1), Collections.singletonList(button2)));
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(message.getChatId());
                    sendPhoto.setReplyMarkup(markup);
                    sendPhoto.setPhoto(new InputFile(user.getFieldId()));
                    sendPhoto.setParseMode(ParseMode.MARKDOWN);
                    sendPhoto.setCaption(localizeService.translateBot("message.bot.menu.picture-caption", user.getLanguage()).replace("FIRST_NAME", user.getFirstName()).replace("LAST_NAME", user.getLastName()).replace("PHONE_NUMBER", user.getPhoneNumber()));
                    botController.execute(sendPhoto);
                    user.setState(BotState.PROFILE);
                    userRepository.save(user);
                } else {
                    conflict(update, user.getLanguage());
                }
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }


    public void tgProfile(Update update, DbUser user, boolean toCheck) throws TelegramApiException {
        Message message = getMessage(update);
        if (toCheck) {
            if (message.hasText()) {
                if (message.getText().equals(localizeService.translateBot("message.bot.menu.button1", user.getLanguage()))) {
                    tgVerification(update, user, false);
                } else if (message.getText().equals(localizeService.translateBot("message.bot.menu.button2", user.getLanguage()))) {
                    tgGetLastName(update, user, false);
                } else {
                    conflict(update, user.getLanguage());
                }
            } else {
                conflict(update, user.getLanguage());
            }
        }
    }

    public void conflict(Update update, String language) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setText(localizeService.translateBot("message.error.bot.replace", Objects.isNull(language) ? "eng" : language));
            botController.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("CONFLICT {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void typing(Update update) {
        try {
            SendChatAction sendChatAction = new SendChatAction();
            sendChatAction.setChatId(String.valueOf(update.getMessage().getChatId()));
            sendChatAction.setAction(ActionType.TYPING);
            botController.execute(sendChatAction);
        } catch (Exception e) {
            log.error("TYPING {}", e.getMessage());
        }
    }

    private Message getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        } else {
            return update.getCallbackQuery().getMessage();
        }
    }

    private ReplyKeyboardMarkup createMarkup(List<List<KeyboardButton>> rows) {
        List<KeyboardRow> rowList = new ArrayList<>();
        for (List<KeyboardButton> row : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.addAll(row);
            rowList.add(keyboardRow);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(rowList);
        markup.setResizeKeyboard(true);
        return markup;
    }

    private InlineKeyboardMarkup createInlineMarkup(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}
