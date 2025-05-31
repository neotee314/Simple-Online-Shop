package com.neotee.ecommercesystem.solution.client.application.controller;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.solution.client.application.dto.ClientDTO;
import com.neotee.ecommercesystem.solution.client.application.service.ClientApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientApplicationService clientApplicationService;

    @Operation(summary = "Get Client by Id", description = "Returns the client for the given Email")
    @GetMapping
    public ResponseEntity<ClientDTO> getClientByEmail(@RequestParam(required = false) String email) {
        ClientDTO clientDTO = clientApplicationService.findClientDTOByEmail(email);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }
}
