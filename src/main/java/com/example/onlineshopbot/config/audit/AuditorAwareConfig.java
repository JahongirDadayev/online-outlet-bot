package com.example.onlineshopbot.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditorAwareConfig {
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new AuditorAwareImpl();
    }
}
