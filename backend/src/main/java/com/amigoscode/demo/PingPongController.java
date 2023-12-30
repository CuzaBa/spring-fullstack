package com.amigoscode.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PingPongController {
    record PingPong(String result){}

    @GetMapping("ping")
    public PingPong getPingPong(){
        return new PingPong("Pong");
    }
}