package com.example.onlineshopbot.config.localize;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

@Configuration
public class LocalizeConfig extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-language");
        return Objects.isNull(headerLang) || headerLang.isEmpty() ? Locale.getDefault() : Locale.forLanguageTag(headerLang);
    }
}