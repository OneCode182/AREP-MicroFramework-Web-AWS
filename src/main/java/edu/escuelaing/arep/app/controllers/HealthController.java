package edu.escuelaing.arep.app.controllers;

import edu.escuelaing.arep.annotations.GetMapping;
import edu.escuelaing.arep.annotations.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}

