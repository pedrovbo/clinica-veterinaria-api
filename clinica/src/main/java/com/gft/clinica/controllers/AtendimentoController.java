package com.gft.clinica.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gft.clinica.dtos.AtendimentoDTO;
import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.services.AtendimentoService;

@RestController
@RequestMapping("v1/atendimentos")
public class AtendimentoController {

        @Autowired
        ModelMapper modelMapper;

        @Autowired
        AtendimentoService atendimentoService;

        @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
        @PostMapping
        public ResponseEntity<AtendimentoDTO> salvarAtendimento(
                        @RequestParam(value = "veterinario", defaultValue = "0") Long id_veterinario,
                        @RequestParam(value = "cliente", defaultValue = "0") Long id_cliente,
                        @RequestParam(value = "cachorro", defaultValue = "0") Long id_cachorro,
                        @RequestBody AtendimentoDTO dto) {

                Atendimento atendimento = atendimentoService.salvarAtendimento(modelMapper.map(dto, Atendimento.class),
                                id_veterinario, id_cliente, id_cachorro);

                return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(atendimento, AtendimentoDTO.class));

        }

        @GetMapping("/{id}")
        public ResponseEntity<AtendimentoDTO> buscarAtendimento(@PathVariable Long id) {

                Atendimento atendimento = atendimentoService.buscarAtendimento(id);

                return ResponseEntity.ok(modelMapper.map(atendimento, AtendimentoDTO.class));
        }

        @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
        @GetMapping
        public ResponseEntity<Page<AtendimentoDTO>> buscarTodosAtendimentos(
                        @PageableDefault Pageable pageable) {

                return ResponseEntity
                                .ok(atendimentoService.listarTodosAtendimentos(pageable)
                                                .map(i -> modelMapper.map(i, AtendimentoDTO.class)));

        }

        @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
        @PutMapping("/{id}")
        public ResponseEntity<AtendimentoDTO> alterarAtendimento(@RequestBody AtendimentoDTO dto, @PathVariable Long id,
                        @RequestParam(value = "veterinario", defaultValue = "0") Long id_veterianrio,
                        @RequestParam(value = "cliente", defaultValue = "0") Long id_cliente,
                        @RequestParam(value = "cachorro", defaultValue = "0") Long id_cachorro) {

                Atendimento atendimento = atendimentoService.atualizarAtendimento(
                                modelMapper.map(dto, Atendimento.class), id, id_veterianrio, id_cliente,
                                id_cachorro);
                return ResponseEntity.ok(modelMapper.map(atendimento, AtendimentoDTO.class));

        }

        @PreAuthorize("hasAuthority('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<AtendimentoDTO> excluirAtendimento(@PathVariable Long id) {

                atendimentoService.excluirAtendimento(id);
                return ResponseEntity.noContent().build();
        }
}
