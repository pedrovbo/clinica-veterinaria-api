package com.gft.clinica.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DadosCachorroDia {

        private String nome;

        private Integer idade;

        private Double tamanho;

        private Double peso;


}
