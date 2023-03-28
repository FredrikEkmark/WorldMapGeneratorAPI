package com.fredrik.worldMapGenerator.Controller;

import com.fredrik.worldMapGenerator.tile.MapGeneration;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/")
    String sendData() {
        return MapGeneration.mapGeneration();
    }
}
