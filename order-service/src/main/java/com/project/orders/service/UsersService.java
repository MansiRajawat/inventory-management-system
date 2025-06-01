package com.project.orders.service;

import com.project.orders.dao.UsersRepository;
import com.project.orders.helper.JwtUtil;
import com.project.orders.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class UsersService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(Users user) {
        Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return "User already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(Users user) {
        Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            boolean isPasswordValid = passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword());
            if (isPasswordValid) {
                return jwtUtil.generateToken(user.getUsername());
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }


}
