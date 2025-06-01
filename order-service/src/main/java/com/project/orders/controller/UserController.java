package com.project.orders.controller;

import com.project.orders.model.Users;
import com.project.orders.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users users) {
        return ResponseEntity.ok(usersService.registerUser(users));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users users) {
        String token = usersService.loginUser(users);
        return ResponseEntity.ok(token);
    }
}
