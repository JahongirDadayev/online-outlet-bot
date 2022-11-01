package com.example.onlineshopbot.service;

import com.example.onlineshopbot.config.publisher.PublisherEvent;
import com.example.onlineshopbot.entity.DbUser;
import com.example.onlineshopbot.model.EmailModel;
import com.example.onlineshopbot.model.UserModel;
import com.example.onlineshopbot.model.result.ApiResponse;
import com.example.onlineshopbot.model.result.JwtModel;
import com.example.onlineshopbot.repository.UserRepository;
import com.example.onlineshopbot.utils.GenerationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PublisherEvent publisherEvent;

    private final SecurityService securityService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final GenerationCode generationCode;

    private final LocalizeService localizeService;

    public ApiResponse signIn(UserModel userModel) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userModel.getUsername(), userModel.getPassword()));
            DbUser user = (DbUser) authenticate.getPrincipal();
            return new ApiResponse(null, new JwtModel(securityService.generationToken(user.getUsername(), user.getAuthorities())), true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(localizeService.translate("message.error.auth.sign-in.invalid-password-or-login"), null, false);
        }
    }

    public ApiResponse forgotPassword(UserModel userModel) {
        Optional<DbUser> optionalDbUser = userRepository.findByUsername(userModel.getUsername());
        if (optionalDbUser.isPresent()) {
            String password = generationCode.generationCode();
            DbUser user = optionalDbUser.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            publisherEvent.setPublisher(new EmailModel(user.getUsername(), password, localizeService.translate("message.auth.forgot-password.sign-in-account.subject"), localizeService.translate("message.auth.forgot-password.sign-in-account.message")));
            return new ApiResponse(null, null, true);
        } else {
            return new ApiResponse(localizeService.translate("message.error.user-not-found"), null, false);
        }
    }
}