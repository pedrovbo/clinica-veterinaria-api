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
import com.gft.clinica.dtos.VeterinarioDTO;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.services.VeterinarioService;

@RestController
@RequestMapping("v1/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<VeterinarioDTO> salvarVeterinario(@RequestBody VeterinarioDTO dto) {

        Veterinario veterinario = veterinarioService.salvarVeterinario(modelMapper.map(dto, Veterinario.class));

        return ResponseEntity.ok(modelMapper.map(veterinario, VeterinarioDTO.class));

    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> buscarVeterinario(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(veterinarioService.buscarVeterinario(id), VeterinarioDTO.class));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @GetMapping
    public ResponseEntity<Page<VeterinarioDTO>> buscarTodosOsVeterinarios(@PageableDefault Pageable pageable) {

        return ResponseEntity.ok(veterinarioService.listarTodosOsVeterinarios(pageable)
                .map(i -> modelMapper.map(i, VeterinarioDTO.class)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> atualizarVeterinario(@RequestBody VeterinarioDTO dto, @PathVariable Long id) {

        Veterinario veterinario = veterinarioService.atualizarVeterinario(modelMapper.map(dto, Veterinario.class), id);

        return ResponseEntity.ok(modelMapper.map(veterinario, VeterinarioDTO.class));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ResponseEntity<VeterinarioDTO> excluirVeterianario(@PathVariable Long id) {

        veterinarioService.excluirVeterinario(id);
        return ResponseEntity.noContent().build();

    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @GetMapping("/{id}/atendimentos")
    public ResponseEntity<Page<AtendimentoDTO>> listarTodosOsAtendimentosDoVeterinario(@PathVariable Long id,
            @PageableDefault Pageable pageable) {

        return ResponseEntity.ok(veterinarioService.listarTodosOsAtendimentosDoVeterinario(id, pageable)
                .map(i -> modelMapper.map(i, AtendimentoDTO.class)));
    }

}
