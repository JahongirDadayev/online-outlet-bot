package com.example.onlineshopbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LocalizeService {
    private final ResourceBundleMessageSource messageSource;

    public String translate(String messageCode){
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }

    public String translateBot(String messageCode, String languageTag){
        return messageSource.getMessage(messageCode,null, Locale.forLanguageTag(languageTag));
    }
}