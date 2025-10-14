package com.upely.secretconfig.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author dht31261
 * @date 2025年10月12日 20:14:12
 */
@RestController
public class SysController {

    @GetMapping(path = { "/checkstatus", "/health" })
    public String checkstatus(HttpServletRequest request) {
        return "success";
    }
}