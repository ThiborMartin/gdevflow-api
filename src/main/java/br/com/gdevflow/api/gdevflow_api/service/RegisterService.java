package br.com.gdevflow.api.gdevflow_api.service;

import br.com.gdevflow.api.gdevflow_api.dto.RegisterRequestDTO;
import br.com.gdevflow.api.gdevflow_api.model.Role;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequestDTO dto) {
        // Normaliza o email para evitar problemas com maiúsculas/minúsculas e espaços
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        User user = new User(
            dto.getName(),
            email,
            passwordEncoder.encode(dto.getPassword()),
            Role.DEV
        );

        userRepository.save(user);
    }
}
