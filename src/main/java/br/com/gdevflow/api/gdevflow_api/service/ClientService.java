package br.com.gdevflow.api.gdevflow_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gdevflow.api.gdevflow_api.dto.ClientSearchResponse;
import br.com.gdevflow.api.gdevflow_api.exception.ForbiddenOperationException;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import br.com.gdevflow.api.gdevflow_api.security.AuthenticatedUserService;

@Service
public class ClientService {

    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ClientService(UserRepository userRepository, AuthenticatedUserService authenticatedUserService) {
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional(readOnly = true)
    public List<ClientSearchResponse> searchClientsByEmail(String email) {
        requireFreelancer();
        String normalizedEmail = normalizeEmail(email);

        return userRepository.findAllByEmailContainingIgnoreCaseAndRoleOrderByNameAsc(
                        normalizedEmail,
                        UserRole.CLIENT)
                .stream()
                .map(ClientSearchResponse::fromEntity)
                .toList();
    }

    private void requireFreelancer() {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (currentUser.getRole() != UserRole.FREELANCER) {
            throw new ForbiddenOperationException("Apenas freelancers podem buscar clientes");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um email para buscar clientes");
        }

        return email.trim();
    }
}
