package com.vidacruz.triage.infraestructura.adaptador.out.persistencia.entidades;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NivelTriage {
    CRITICO(1),
    URGENTE(2),
    URGENTE_2(3),
    NO_URGENTE(4),
    NO_URGENTE_2(5);

    private final int nivel;
}