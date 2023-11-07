package com.example.registeruser.verification;

import com.example.registeruser.model.User;

import java.util.Optional;

public interface IVerificationTokenService {

    String validateUserToke(String token);

    void saveUserVerificationToken(String token, User user);

    Optional<VerificationToken> findByVerificationToken(String token);
}
