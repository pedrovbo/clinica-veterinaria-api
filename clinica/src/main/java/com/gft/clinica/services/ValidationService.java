package com.gft.clinica.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gft.clinica.entities.Cliente;
import com.gft.clinica.entities.Perfil;
import com.gft.clinica.entities.Veterinario;
import com.gft.clinica.exception.ClinicaException;
import com.gft.clinica.repositories.ClienteRepository;
import com.gft.clinica.repositories.VeterinarioRepository;

@Service
public class ValidationService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    protected String validacaoEmail(String email) {

        if (clienteRepository.findByEmail(email).isPresent() || veterinarioRepository.findByEmail(email).isPresent()) {

            throw new ClinicaException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        return email;
    }

    protected Cliente validacaoPerfilCliente(Cliente cliente, List<Perfil> perfis) {

        List<Perfil> perfil = List.of(new Perfil(2L, "CLIENTE"));

        if (!(perfis.equals(perfil))) {
            throw new ClinicaException("Cliente não pode ter mais de 1 perfil ou ser adminstrador",
                    HttpStatus.CONFLICT);
        } else {
            return cliente;
        }
    }

    protected Veterinario validacaoPerfilVeterinario(Veterinario veterinario, List<Perfil> perfis) {

        Perfil perfil = new Perfil(3L, "VETERINARIO");

        if (!(perfis.contains(perfil))) {
            throw new ClinicaException("Obrigatorio ter perfil veterinario", HttpStatus.CONFLICT);

        } else {
            return veterinario;
        }
    }

}
