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
import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.repositories.AtendimentoRepository;
import com.gft.clinica.repositories.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ValidationService validationService;

    @Autowired
    AtendimentoRepository atendimentoRepository;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder encoder;

    @Transactional
    public Cliente salvarCliente(Cliente cliente) {

        List<Perfil> perfis = cliente.getPerfis();

        cliente.setSenha(encoder.encode(cliente.getSenha()));

        validationService.validacaoEmail(cliente.getEmail());
        validationService.validacaoPerfilCliente(cliente, perfis);

        return clienteRepository.save(cliente);
    }

    public Cliente buscarCliente(Long id) {

        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

    }

    public Page<Cliente> listarTodosOsClientes(Pageable pageable) {

        return clienteRepository.findAll(pageable);
    }

    @Transactional
    public Cliente atualizarCliente(Cliente cliente, Long clienteId) {

        Cliente origCliente = clienteRepository.findById(clienteId).get();

        origCliente.setNome(cliente.getNome());
        origCliente.setEmail(cliente.getEmail());
        origCliente.setTelefone(cliente.getTelefone());
        origCliente.setEndereco(cliente.getEndereco());

        return clienteRepository.save(origCliente);
    }
    @Transactional
    public void excluirCliente(Long id) {

        Cliente clienteOriginal = this.buscarCliente(id);

        clienteRepository.delete(clienteOriginal);
    }

    public Cliente buscarClientePorEmail(String email) {
        Optional<Cliente> cliente = clienteRepository.findByEmail(email);

        if (!cliente.isPresent()) {
            throw new UsernameNotFoundException("Cliente não encontrado");
        }

        return cliente.get();

    }

    public Page<Atendimento> listarTodosOsAtendimentosDoCliente(Long cliente_id, Pageable pageable) {
        return atendimentoRepository.findAllByClienteId(cliente_id, pageable);
    }

}
