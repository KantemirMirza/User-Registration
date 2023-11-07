package com.example.registeruser.service;

import com.example.registeruser.model.Role;
import com.example.registeruser.model.User;
import com.example.registeruser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public List<User> listOfUsers(){
        return userRepository.findAll();
    }
    @Override
    public User saveUser(User userDto) {
        User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));
                userRepository.save(user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found: "));
    }

}
