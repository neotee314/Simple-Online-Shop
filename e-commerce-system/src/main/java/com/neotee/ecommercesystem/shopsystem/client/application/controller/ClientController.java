package com.neotee.ecommercesystem.shopsystem.client.application.controller;

import com.neotee.ecommercesystem.shopsystem.client.application.dto.ClientDTO;
import com.neotee.ecommercesystem.shopsystem.client.application.service.ClientApplicationService;
import com.neotee.ecommercesystem.shopsystem.shoppingbasket.application.dto.ShoppingBasketDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@Tag(name = "Client Management")
@RequiredArgsConstructor
public class ClientController {
    private final ClientApplicationService clientApplicationService;

    @Operation(summary = "Get All Clients", description = "Returns all clients")
    @GetMapping("/all")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clientDTOS = clientApplicationService.getAll();
        return new ResponseEntity<>(clientDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Get Client by Email", description = "Returns the client for the given Email")
    @GetMapping
    public ResponseEntity<ClientDTO> getClientByEmail(@RequestParam(name = "email", required = false) String email) {
        ClientDTO clientDTO = clientApplicationService.findClientDTOByEmail(email);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }
    @Operation(summary = "Get Client by Id", description = "Returns the client for the given Id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable UUID id) {
        ClientDTO clientDTO = clientApplicationService.findById(id);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get ShoppingBasket of a Client", description = "Returns the ShoppingBasket of Client for the given Id")
    @GetMapping(value = "/{clientId}/basket")
    public ResponseEntity<ShoppingBasketDTO> getBasketOfClient(@PathVariable UUID clientId) {
        ShoppingBasketDTO shoppingBasketDTO = clientApplicationService.getBasketOf(clientId);
        return new ResponseEntity<>(shoppingBasketDTO, HttpStatus.OK);
    }

    @Operation(summary = "Register a client", description = "Add a client to System")
    @PostMapping
    public ResponseEntity<Void> registerClient(@RequestBody ClientDTO clientDTO) {
        clientApplicationService.register(clientDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Change Address", description = "Change address of Client")
    @PatchMapping
    public ResponseEntity<Void> updateAddress(@RequestBody ClientDTO clientDTO) {
        clientApplicationService.updateAddress(clientDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
