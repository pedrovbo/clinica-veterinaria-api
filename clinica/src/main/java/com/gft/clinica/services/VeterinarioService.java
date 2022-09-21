package com.gft.clinica.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gft.clinica.entities.Atendimento;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder encoder;

    @Transactional
    public Veterinario salvarVeterinario(Veterinario veterinario) {

        List<Perfil> perfis = veterinario.getPerfis();

        veterinario.setSenha(encoder.encode(veterinario.getSenha()));
        validationService.validacaoEmail(veterinario.getEmail());
        validationService.validacaoPerfilVeterinario(veterinario, perfis);

        return veterinarioRepository.save(veterinario);

    }

    public Veterinario buscarVeterinario(Long id) {

        Optional<Veterinario> veterinario = veterinarioRepository.findById(id);

        return veterinario.orElseThrow(() -> new EntityNotFoundException("Veterinário não encontrado"));
    }

    public Page<Veterinario> listarTodosOsVeterinarios(Pageable pageable) {

        return veterinarioRepository.findAll(pageable);
    }

    @Transactional
    public Veterinario atualizarVeterinario(Veterinario veterinario, Long id) {

        Veterinario veterinarioOriginal = this.buscarVeterinario(id);

        veterinarioOriginal.setNome(veterinario.getNome());
        veterinarioOriginal.setEmail(veterinario.getEmail());
        veterinarioOriginal.setTelefone(veterinario.getTelefone());
        veterinarioOriginal.setEndereco(veterinario.getEndereco());
        veterinarioOriginal.setCrmv(veterinario.getCrmv());

        return veterinarioRepository.save(veterinarioOriginal);
    }

    @Transactional
    public void excluirVeterinario(Long id) {

        Veterinario veterinario = this.buscarVeterinario(id);
        veterinarioRepository.delete(veterinario);
    }

    public Veterinario buscarVeterinarioPorEmail(String email) {
        Optional<Veterinario> veterinario = veterinarioRepository.findByEmail(email);

        if (!veterinario.isPresent()) {
            throw new UsernameNotFoundException("Veterinário não encontrado");
        }

        return veterinario.get();
    }

    public Page<Atendimento> listarTodosOsAtendimentosDoVeterinario(Long veterinario_id, Pageable pageable) {
        return atendimentoRepository.findAllByVeterinarioId(veterinario_id, pageable);
    }

}
