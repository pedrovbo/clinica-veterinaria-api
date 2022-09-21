package com.gft.clinica.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gft.clinica.entities.Cachorro;
import com.gft.clinica.repositories.CachorroRepository;

@Service
public class CachorroService {

    @Autowired
    private CachorroRepository cachorroRepository;

    @Autowired
    private ClienteService clienteService;

    @Transactional
    public Cachorro salvarCachorro(Cachorro cachorro, Long id_cliente) {

        cachorro.setCliente(clienteService.buscarCliente(id_cliente));

        return cachorroRepository.save(cachorro);
    }

    public Cachorro buscarCachorro(Long id) {
        Optional<Cachorro> optional = cachorroRepository.findById(id);

        return optional.orElseThrow(() -> new EntityNotFoundException("Cachorro não encontrado"));

    }

    public Page<Cachorro> listarTodosCachorros(Pageable pageable) {

        return cachorroRepository.findAll(pageable);
    }

    @Transactional
    public Cachorro atualizarCachorro(Cachorro cachorro, Long id) {

        Cachorro cachorroOriginal = this.buscarCachorro(id);

        cachorro.setId(cachorroOriginal.getId());

        return cachorroRepository.save(cachorro);
    }

    @Transactional
    public void excluirCachorro(Long id) {

        Cachorro cachorro = this.buscarCachorro(id);

        cachorroRepository.delete(cachorro);
    }
}
