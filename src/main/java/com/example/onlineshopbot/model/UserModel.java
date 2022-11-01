package com.example.onlineshopbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "password")
    private String password;
}