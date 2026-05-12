package br.com.gdevflow.api.gdevflow_api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.gdevflow.api.gdevflow_api.dto.LoginRequestDTO;
import br.com.gdevflow.api.gdevflow_api.dto.LoginResponseDTO;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.security.JwtUtil;
import br.com.gdevflow.api.gdevflow_api.security.UserDetailsImpl;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        UserRole role = ((UserDetailsImpl) authentication.getPrincipal()).getRole();

        return new LoginResponseDTO(token, role);
    }
}
