package com.gft.clinica.dtos;

import java.io.Serializable;

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
public class DadosCachorroDiaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;

    private Integer idade;

    private Double tamanho;

    private Double peso;

}
