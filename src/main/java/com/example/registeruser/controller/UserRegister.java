package com.example.registeruser.controller;

import com.example.registeruser.model.RegisterEvent;
import com.example.registeruser.model.User;
import com.example.registeruser.resetPassword.IPasswordResetService;
import com.example.registeruser.service.IUserService;
import com.example.registeruser.utility.RegisterEventListener;
import com.example.registeruser.utility.UrlUtil;
import com.example.registeruser.verification.IVerificationTokenService;
import com.example.registeruser.verification.VerificationToken;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserRegister {
    private final IUserService userService;
    private final ApplicationEventPublisher publisher;
    private final IVerificationTokenService verificationTokenService;
    private final IPasswordResetService passwordResetService;

    private final RegisterEventListener eventListener;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute("user") User user,
                               BindingResult result, HttpServletRequest request){

        if(!user.getPassword().equals(user.getConfirmPassword())){
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "redirect:/register";
        }

        User createUser = userService.saveUser(user);
        publisher.publishEvent(new RegisterEvent(createUser, UrlUtil.getRequestUrl(request)));

        return "redirect:/register?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        Optional<VerificationToken> theToken = verificationTokenService.findByVerificationToken(token);
        if(theToken.isPresent() && theToken.get().getUser().isEnable()){
            return "redirect:/login?verified";
        }
        String verificationResult = verificationTokenService.validateUserToke(token);
        switch(verificationResult.toLowerCase()){
            case"expired":
                return "redirect:/error?expired";
            case"valid":
                return "redirect:/login?valid";
            default:
                return "redirect:/error?invalid";
        }
    }

    @GetMapping("/forgetPassword")
    public String forgetPassword(){
        return "forgetPassword";
    }

    @PostMapping("/forgetPassword")
    public String resetPassword(HttpServletRequest request, Model model){

        String email = request.getParameter("email");

        User user = userService.findByEmail(email);

        if(user == null){
            return "redirect:/forgetPassword?not_found";
        }

        String passwordResetToken = UUID.randomUUID().toString();

        passwordResetService.createPasswordResetTokenForUser(user, passwordResetToken);

       String url = UrlUtil.getRequestUrl(request) + "/forgetPassword/password-reset-form?token=" + passwordResetToken;

        try {
            eventListener.sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/forgetPassword?success";
    }

    @GetMapping("/forgetPassword/password-reset-form")
    public String passwordResetForm(@RequestParam("token")String token, Model model){
        model.addAttribute("token", token);

        return"passwordResetForm";
    }

    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest request){
        String theToken = request.getParameter("token");
        String password = request.getParameter("password");
        String tokenVerificationResult = passwordResetService.validatePasswordResetToken(theToken);
        if(tokenVerificationResult.equalsIgnoreCase("valid")){
            return "redirect:/error?invalid_token";
        }
        Optional<User> theUser = passwordResetService.findUserByPasswordResetToken(theToken);
        if(theUser.isEmpty()){
            passwordResetService.resetPassword(theUser.get(), password);
            return"redirect:/login?reset_success";
        }
        return"redirect:/error?not_found";
    }

    //LOGIN FORM

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }
}
