package com.gft.clinica.dtos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gft.clinica.dtos.dogapi.DadosRacaDogApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CachorroDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nome;

    private String raca;

    private Integer idade;

    private Double tamanho;

    private String porte;

    private Double peso;

    private String sexo;

    private String nomeCliente;

    private List<DadosRacaDogApi> dadosRacaDogApi;

    private List<AtendimentoDTO> atendimentos;

    public CachorroDTO(Long id, String nome, String raca, Integer idade, Double tamanho, String porte, Double peso,
            String sexo, ClienteDTO nomeCliente, List<AtendimentoDTO> atendimentos,
            List<DadosRacaDogApi> dadosRacaDogApi) {
        this.id = id;
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.tamanho = tamanho;
        this.porte = porte;
        this.peso = peso;
        this.sexo = sexo;
        this.nomeCliente = nomeCliente.getNome();
        this.atendimentos = atendimentos;
        this.dadosRacaDogApi = dadosRacaDogApi;
    }

}
