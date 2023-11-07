package com.example.registeruser.resetPassword;

import com.example.registeruser.model.User;

import java.util.Optional;

public interface IPasswordResetService {
    String validatePasswordResetToken(String theToken);

    Optional<User> findUserByPasswordResetToken(String theToken);

    void resetPassword(User user, String password);

    void createPasswordResetTokenForUser(User user, String passwordResetToken);
}
