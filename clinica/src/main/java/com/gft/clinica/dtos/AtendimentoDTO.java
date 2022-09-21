package com.gft.clinica.dtos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AtendimentoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String titulo;
    private String descricaoAtendimento;
    private String diagnostico;
    private String comentarios;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    
    private String data ;
    private String nomeVeterinario;
    private String nomeCachorro;
    private String nomeCliente;
    private DadosCachorroDiaDTO dadosCachorroDia;

    public AtendimentoDTO(String titulo, String descricaoAtendimento, String diagnostico, String comentarios,

            String data, VeterinarioDTO veterinario, CachorroDTO cachorro, ClienteDTO cliente,

            DadosCachorroDiaDTO dadosCachorroDia) {

        this.titulo = titulo;

        this.descricaoAtendimento = descricaoAtendimento;

        this.diagnostico = diagnostico;

        this.comentarios = comentarios;

        this.data = data;

        this.nomeVeterinario = veterinario.getNome();

        this.nomeCachorro = cachorro.getNome();

        this.nomeCliente = cliente.getNome();

        this.dadosCachorroDia = dadosCachorroDia;

    }

}
