package com.upely.secretconfig.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upely.secretconfig.service.UserService;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private UserService userService;

    @GetMapping("/hasUser")
    public Boolean hasUser() {
        return userService.hasUser();
    }

    @PostMapping("/register")
    public void register() {
        // TODO
    }
}