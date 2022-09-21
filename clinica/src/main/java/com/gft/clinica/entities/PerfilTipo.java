package com.gft.clinica.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerfilTipo {

    ADMIN(1L, "ADMIN"), CLIENTE(2L, "CLIENTE"), VETERINARIO(3L, "VETERINARIO");

    private Long codigo;

    private String descricao;

}
