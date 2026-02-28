package br.com.gdevflow.api.gdevflow_api.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.gdevflow.api.gdevflow_api.dto.RegisterRequestDTO;
import br.com.gdevflow.api.gdevflow_api.service.RegisterService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterService registerService;

    public AuthController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequestDTO dto) {
        registerService.register(dto);
    }
}