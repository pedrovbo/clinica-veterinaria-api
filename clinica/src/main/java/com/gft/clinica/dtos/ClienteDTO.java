package com.gft.clinica.dtos;

import java.io.Serializable;
import java.util.List;

import com.gft.clinica.entities.Perfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String telefone;

    private EnderecoDTO endereco;

    private List<CachorroDTO> cachorros;

    private List<Perfil> perfis;

}
