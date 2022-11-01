package com.example.onlineshopbot.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "object")
    private Object data;

    @JsonProperty(value = "success")
    private Boolean success;
}
