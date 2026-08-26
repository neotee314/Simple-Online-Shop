package com.neotee.ecommercesystem.shopsystem.client.application.controller;

import com.neotee.ecommercesystem.domainprimitives.ClientId;
import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientRequestDto;
import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientResponseDto;
import com.neotee.ecommercesystem.shopsystem.client.application.mapper.ClientMapper;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientApplicationService;
import com.neotee.ecommercesystem.shopsystem.client.domain.Client;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientApplicationService clientService;
    private final ClientMapper clientMapper;

    
    @Operation(summary = "Get All Clients")
    @GetMapping("/all")
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(
                clientService.findAll().stream()
                        .map(clientMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Operation(summary = "Get Client by Email")
    @GetMapping
    public ResponseEntity<ClientResponseDto> getClientByEmail(@RequestParam(required = false) String email) {
        if (email == null || email.isEmpty())
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        var client = clientService.findByEmail(Email.of(email));
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @Operation(summary = "Get Client by Id")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable ClientId id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @Operation(summary = "Register a client")
    @PostMapping
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientRequestDto request) {
        Client client = clientService.registerClient(
                request.name(),
                Email.of(request.email()),
                (HomeAddress) HomeAddress.of(request.street(), request.city(), ZipCode.of(request.zipCode()))
        );
        return new ResponseEntity<>(clientMapper.toDto(client), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Client")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable ClientId id,
            @Valid @RequestBody ClientRequestDto request) {

        var client = clientService.updateClient(
                id,
                request.name(),
                Email.of(request.email()),
                (HomeAddress) HomeAddress.of(request.street(), request.city(), ZipCode.of(request.zipCode()))
        );
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @Operation(summary = "Delete Client")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable ClientId id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}