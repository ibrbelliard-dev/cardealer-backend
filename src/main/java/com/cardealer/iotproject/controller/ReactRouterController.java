package com.cardealer.iotproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactRouterController {
    
    @RequestMapping(value = {"/GoldPremierAuto/**", "/GoldPremierAuto"})
    public String forwardToReact() {
        return "forward:/GoldPremierAuto/index.html";
    }
}