package br.com.gdevflow.api.gdevflow_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.LoginRequestDTO;
import br.com.gdevflow.api.gdevflow_api.dto.LoginResponseDTO;
import br.com.gdevflow.api.gdevflow_api.dto.RegisterRequestDTO;
import br.com.gdevflow.api.gdevflow_api.service.AuthService;
import br.com.gdevflow.api.gdevflow_api.service.RegisterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;

    public AuthController(RegisterService registerService,
                          AuthService authService) {
        this.registerService = registerService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequestDTO dto) {
        registerService.register(dto);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO dto) {
        return authService.login(dto);
    }
}