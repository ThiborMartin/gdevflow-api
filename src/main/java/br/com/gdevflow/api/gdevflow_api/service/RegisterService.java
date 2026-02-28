package br.com.gdevflow.api.gdevflow_api.service;

import br.com.gdevflow.api.gdevflow_api.dto.RegisterRequestDTO;
import br.com.gdevflow.api.gdevflow_api.model.Role;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        System.out.println("Procurando email %s no banco de dados. Retorno: %b".formatted(dto.getEmail(), userRepository.existsByEmail(dto.getEmail())));
        if (userRepository.existsByEmail(dto.getEmail())) {
            
            System.out.println("Email já cadastrado: " + dto.getEmail());
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User(
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                Role.DEV
        );

        userRepository.save(user);
    }
}
