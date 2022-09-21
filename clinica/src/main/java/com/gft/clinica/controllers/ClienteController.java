package com.gft.clinica.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.dtos.ClienteDTO;
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.services.ClienteService;

@RestController
@RequestMapping("v1/clientes")
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @Autowired
    ModelMapper modelMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@RequestBody ClienteDTO dto) {

        Cliente cliente = clienteService.salvarCliente(modelMapper.map(dto, Cliente.class));
        return ResponseEntity.ok(modelMapper.map(cliente, ClienteDTO.class));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarCliente(@PathVariable Long id) {

        Cliente cliente = clienteService.buscarCliente(id);

        return ResponseEntity.ok(modelMapper.map(cliente, ClienteDTO.class));

    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> listarTodosOsClientes(@PageableDefault Pageable pageable) {

        return ResponseEntity.ok(clienteService.listarTodosOsClientes(pageable)
                .map(i -> modelMapper.map(i, ClienteDTO.class)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@RequestBody ClienteDTO clienteDTO, @PathVariable Long id) {

        Cliente cliente = clienteService.atualizarCliente(modelMapper.map(clienteDTO, Cliente.class), id);

        return ResponseEntity.ok(modelMapper.map(cliente, ClienteDTO.class));

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteDTO> excluirCliente(@PathVariable Long id) {

        clienteService.excluirCliente(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}/atendimentos")
    public ResponseEntity<Page<AtendimentoDTO>> listarTodosOsAtendimentosDoCliente(@PathVariable Long id,
            @PageableDefault Pageable pageable) {

        return ResponseEntity.ok(clienteService.listarTodosOsAtendimentosDoCliente(id, pageable)
                .map(i -> modelMapper.map(i, AtendimentoDTO.class)));
    }

}
