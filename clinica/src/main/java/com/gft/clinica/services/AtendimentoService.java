package com.gft.clinica.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.CachorroRepository;
import com.gft.clinica.repositories.ClienteRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@Service
public class AtendimentoService {

    @Autowired
    AtendimentoRepository atendimentoRepository;

    @Autowired
    VeterinarioRepository veterinarioRepository;

    @Autowired
    CachorroRepository cachorroRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Transactional
    public Atendimento salvarAtendimento(Atendimento atendimento, Long id_veterinario, Long id_cliente,
            Long id_cachorro) {

        atendimento.setVeterinario(veterinarioRepository.findById(id_veterinario).get());
        atendimento.setCachorro(cachorroRepository.findById(id_cachorro).get());
        atendimento.setCliente(clienteRepository.findById(id_cliente).get());

        return atendimentoRepository.save(atendimento);

    }

    public Atendimento buscarAtendimento(Long id) {

        Optional<Atendimento> optional = atendimentoRepository.findById(id);

        return optional.orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado"));
    }

    public Page<Atendimento> listarTodosAtendimentos(Pageable pageable) {

        return atendimentoRepository.findAll(pageable);

    }

    @Transactional
    public Atendimento atualizarAtendimento(Atendimento atendimento, Long id, Long id_cliente, Long id_cachorro,
            Long id_veterinario) {

        Atendimento atendimentoOriginal = this.buscarAtendimento(id);
        atendimento.setId(atendimentoOriginal.getId());
        atendimento.setCliente(clienteRepository.findById(id_cliente).get());
        atendimento.setCachorro(cachorroRepository.findById(id_cachorro).get());
        atendimento.setVeterinario(veterinarioRepository.findById(id_veterinario).get());

        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void excluirAtendimento(Long id) {

        Atendimento atendimento = this.buscarAtendimento(id);

        atendimentoRepository.delete(atendimento);

    }

}
