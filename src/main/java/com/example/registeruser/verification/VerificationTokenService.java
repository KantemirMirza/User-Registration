package com.example.registeruser.verification;

import com.example.registeruser.model.User;
import com.example.registeruser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService{
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String validateUserToke(String token) {
        Optional<VerificationToken> theToken = verificationTokenRepository.findByVerificationToken(token);
        if (theToken.isEmpty()) {
            return "INVALID";
        }
        User user = theToken.get().getUser();

        Calendar calendar = Calendar.getInstance();
        if((theToken.get().getExpirationTime().getTime() - calendar.getTime().getTime()<= 0)){
            return "EXPIRED";
        }

        user.setEnable(true);
        userRepository.save(user);
        return "VALID";
    }

    @Override
    public void saveUserVerificationToken(String token, User user) {
        VerificationToken tokenVerification = new VerificationToken(token, user);
        verificationTokenRepository.save(tokenVerification);
    }

    @Override
    public Optional<VerificationToken> findByVerificationToken(String token) {
        return verificationTokenRepository.findByVerificationToken(token);
    }
}
