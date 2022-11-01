package com.example.onlineshopbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailModel {
    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "subject")
    private String subject;

    @JsonProperty(value = "message")
    private String message;
}