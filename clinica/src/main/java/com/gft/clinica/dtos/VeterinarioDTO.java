package com.gft.clinica.dtos;

import com.gft.clinica.entities.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;



@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VeterinarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String telefone;

    private EnderecoDTO endereco;
    
    private List<AtendimentoDTO> atendimentos;
    
    private List<Perfil> perfis;

    private String crmv;

}
