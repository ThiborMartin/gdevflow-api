package br.com.gdevflow.api.gdevflow_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.ClientSearchResponse;
import br.com.gdevflow.api.gdevflow_api.service.ClientService;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/search")
    public List<ClientSearchResponse> searchClients(@RequestParam(required = false) String email) {
        return clientService.searchClientsByEmail(email);
    }
}
