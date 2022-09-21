package com.gft.clinica.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;

    @Embedded
    private Endereco endereco;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<Perfil> perfis;

    public Usuario(Long id, String email, String senha, List<Perfil> perfis) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.perfis = perfis;
    }



    //Adiciona um novo perfil ao usuário
    public void addPerfil(PerfilTipo perfil) {

        if (this.perfis == null) {

            this.perfis = new ArrayList<>();
        }

        this.perfis.add(new Perfil(perfil.getCodigo()));

    }


}
