package com.example.registeruser.resetPassword;

import com.example.registeruser.model.User;
import com.example.registeruser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService{
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Override
    public String validatePasswordResetToken(String theToken) {

        Optional<PasswordResetToken> passwordResetToken = passwordResetRepository.findByToken(theToken);

        if (passwordResetToken.isEmpty()) {
            return "INVALID";
        }

        Calendar calendar = Calendar.getInstance();
        if((passwordResetToken.get().getExpirationTime().getTime() - calendar.getTime().getTime()<= 0)){
            return "EXPIRED";
        }
        return "VALID";
    }

    @Override
    public Optional<User> findUserByPasswordResetToken(String theToken) {
        return Optional.ofNullable(passwordResetRepository.findByToken(theToken).get().getUser());
    }

    @Override
    public void resetPassword(User theUser, String newPassword){
        theUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(theUser);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordResetToken) {
        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken, user);
        passwordResetRepository.save(resetToken);
    }
}
