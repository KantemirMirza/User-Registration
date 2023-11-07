package com.example.registeruser.controller;

import com.example.registeruser.model.User;
import com.example.registeruser.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/users")
    public String listOfUsers(Model model, Principal principal){
        model.addAttribute("list" , userService.listOfUsers());
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("userDetails", userDetails);
        return "users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("updateUser", userService.findUserById(id));
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("userDetails", userDetails);
        return "editUser";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, @ModelAttribute("updateUser") User user) {
        User existingUser = userService.findUserById(id);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            userService.saveUser(existingUser);
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id){
        User user = userService.findUserById(id);
        userService.deleteUser(user);
        return"redirect:/users";
    }
}
