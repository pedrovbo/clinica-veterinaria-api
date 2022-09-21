package com.gft.clinica.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.repositories.ClienteRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private ClienteService clienteService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (veterinarioRepository.findByEmail(email).isPresent()) {
            return veterinarioService.buscarVeterinarioPorEmail(email);
        } else if (clienteRepository.findByEmail(email).isPresent()) {
            return clienteService.buscarClientePorEmail(email);
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

    }

    public Object buscarUsuarioPorEmail(String email) {
        if (veterinarioRepository.findByEmail(email).isPresent()) {
            Veterinario veterinario = veterinarioService.buscarVeterinarioPorEmail(email);
            return veterinario;
        } else if (clienteRepository.findByEmail(email).isPresent()) {
            Cliente cliente = clienteService.buscarClientePorEmail(email);
            return cliente;
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
    
    }

}
