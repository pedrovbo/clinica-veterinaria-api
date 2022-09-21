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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gft.clinica.dtos.CachorroDTO;
import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.services.CachorroService;
import com.gft.clinica.services.dogapi.DogApiService;

@RestController
@RequestMapping("v1/cachorros")
public class CachorroController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CachorroService cachorroService;

    @Autowired
    private DogApiService dogApiService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @PostMapping
    public ResponseEntity<CachorroDTO> salvarCachorro(
            @RequestParam(value = "cliente", defaultValue = "0") Long id_cliente, @RequestBody CachorroDTO dto) {

        Cachorro cachorro = cachorroService.salvarCachorro(modelMapper.map(dto, Cachorro.class), id_cliente);

        dogApiService.buscarDadosRaca(cachorro.getRaca()).collectList().block();

        CachorroDTO cachorroDTO = modelMapper.map(cachorro, CachorroDTO.class);
        cachorroDTO.setDadosRacaDogApi(dogApiService.buscarDadosRaca(cachorro.getRaca()).collectList().block());
            return ResponseEntity.ok(cachorroDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CachorroDTO> buscarCachorro(@PathVariable Long id) {

        Cachorro cachorro = cachorroService.buscarCachorro(id);
        CachorroDTO dto = modelMapper.map(cachorro, CachorroDTO.class);
        dto.setDadosRacaDogApi(dogApiService.buscarDadosRaca(cachorro.getRaca()).collectList().block());
        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @GetMapping
    public ResponseEntity<Page<CachorroDTO>> listarTodosCachorros(@PageableDefault Pageable pageable) {

        Page<Cachorro> cachorros = cachorroService.listarTodosCachorros(pageable);

        Page<CachorroDTO> cachorroDTOPage = cachorros.map(cachorro -> modelMapper.map(cachorro, CachorroDTO.class));

        for (CachorroDTO cachorroDTO : cachorroDTOPage) {
            cachorroDTO.setDadosRacaDogApi(dogApiService.buscarDadosRaca(cachorroDTO.getRaca()).collectList().block());
        }

        return ResponseEntity.ok(cachorroDTOPage);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<CachorroDTO> alterarCachorro(@RequestBody CachorroDTO dto, @PathVariable Long id) {

        Cachorro cachorro = cachorroService.atualizarCachorro(modelMapper.map(dto, Cachorro.class), id);

        return ResponseEntity.ok(modelMapper.map(cachorro, CachorroDTO.class));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CachorroDTO> excluirCachorro(@PathVariable Long id) {

        cachorroService.excluirCachorro(id);
        return ResponseEntity.noContent().build();

    }
}
