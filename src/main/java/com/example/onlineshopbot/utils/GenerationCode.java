package com.example.onlineshopbot.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerationCode {
    public String generationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
