package br.com.gdevflow.api.gdevflow_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.gdevflow.api.gdevflow_api.dto.RegisterRequestDTO;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequestDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado");
        }

        UserRole role = dto.getRole() == null ? UserRole.FREELANCER : dto.getRole();

        User user = new User(
                dto.getName(),
                email,
                passwordEncoder.encode(dto.getPassword()),
                role);

        userRepository.save(user);
    }
}
