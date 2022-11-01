package com.example.onlineshopbot.config.handler;

import com.example.onlineshopbot.model.result.ApiResponse;
import com.example.onlineshopbot.service.LocalizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandler {
    private final LocalizeService localizeService;

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ApiResponse> handler(RuntimeException exception) {
        log.error(exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(localizeService.translate("message.error.conflict"), null, false));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse> handler(MethodArgumentNotValidException exception) {
        log.error(exception);
        /* Map<String, String> errors = exception.getFieldErrors().stream().filter(fieldError -> Objects.isNull(fieldError.getDefaultMessage())).collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage)); */
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(localizeService.translate("message.error.conflict"), null, false));
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<ApiResponse> handler(AuthenticationException exception) {
        log.error(exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(localizeService.translate("message.error.conflict"), null, false));
    }
}