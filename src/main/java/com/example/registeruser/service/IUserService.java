package com.example.registeruser.service;

import com.example.registeruser.model.User;

import java.util.List;

public interface IUserService {
     List<User> listOfUsers();

    User saveUser(User user);

    User findByEmail(String email);

     User findUserById(Long id);

     void deleteUser(User user);
}
