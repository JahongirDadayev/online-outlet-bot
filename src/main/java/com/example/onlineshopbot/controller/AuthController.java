package com.example.onlineshopbot.controller;

import com.example.onlineshopbot.model.UserModel;
import com.example.onlineshopbot.model.result.ApiResponse;
import com.example.onlineshopbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping(path = "/data")
    public ResponseEntity<ApiResponse> get(){
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("TOGRI", null, true));
    }

    @PostMapping(path = "/sign_in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody UserModel userModel) {
        ApiResponse apiResponse = authService.signIn(userModel);
        return (apiResponse.getSuccess()) ? ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @PostMapping(path = "/forgot_password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody UserModel userModel) {
        ApiResponse apiResponse = authService.forgotPassword(userModel);
        return (apiResponse.getSuccess()) ? ResponseEntity.status(HttpStatus.OK).body(apiResponse) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}