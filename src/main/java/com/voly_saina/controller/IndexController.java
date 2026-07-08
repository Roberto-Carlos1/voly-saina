package com.voly_saina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/route")
    public String route() {
        return "index_true";
    }
}
