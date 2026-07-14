package com.cursojava.ObraControl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ObrasController {

    @PostMapping("/obras")

    public String obras() {
        return "obras";
    }
}
