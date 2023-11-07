package com.example.registeruser.utility;

import com.example.registeruser.model.RegisterEvent;
import com.example.registeruser.model.User;
import com.example.registeruser.verification.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisterEventListener implements ApplicationListener<RegisterEvent> {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;
    private User user;

    @Override
    public void onApplicationEvent(RegisterEvent event) {
        //1. get the user
        user = event.getUser();
        //2. generate a token for the user
        String vToken = UUID.randomUUID().toString();
        //3. save the token for user
        verificationTokenService.saveUserVerificationToken(vToken, user);
        //4. build the verification url
        String url = event.getConfirmationUrl() + "/register/verifyEmail?token=" + vToken;
        //5. send the mail to the user
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {

        String subject = "Email Verification";
        String senderName = "User VerificationService";
        String mailContent = "<p>Hi, " + user.getFirstName() + ", </p> " +
                "<p>Thank You Registering with us, Please, follow the ling below to complete your registration</p>" +
                " <a href=\"" + url + "\">Verify Your email to activate your account</a> " +
                "<p>Thank You, <br> User Registration Portal Service</p>";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    private void emailMessage(String subject, String senderName, String mailContent,
                                     JavaMailSender javaMailSender, User user)throws MessagingException,
            UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("kantemirmirza99@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
