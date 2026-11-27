package com.vidacruz.triage.dominio.entidades;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PacienteTest {

    @ParameterizedTest
    @MethodSource("condicionesCriticas")
    void clasificar_debeRetornarCriticoAnteCualquierCondicionCritica(Consumer<Paciente> condicion) {
        Paciente paciente = pacienteEstable();
        condicion.accept(paciente);

        assertEquals(NivelTriage.CRITICO, paciente.clasificar());
    }

    private static Stream<Arguments> condicionesCriticas() {
        return Stream.of(
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setFrecuenciaCardiaca(39)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setFrecuenciaCardiaca(121)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setSpo2(89)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setPresionSistolica(89)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setGcs(8))
        );
    }

    @ParameterizedTest
    @MethodSource("condicionesUrgentes")
    void clasificar_debeRetornarUrgenteEnLosLimitesDefinidos(Consumer<Paciente> condicion) {
        Paciente paciente = pacienteEstable();
        condicion.accept(paciente);

        assertEquals(NivelTriage.URGENTE, paciente.clasificar());
    }

    private static Stream<Arguments> condicionesUrgentes() {
        return Stream.of(
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setSpo2(90)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setSpo2(93)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setPresionSistolica(90)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setPresionSistolica(100)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setTemperatura(new BigDecimal("38.5"))),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setGcs(9)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setGcs(13))
        );
    }

    @ParameterizedTest
    @MethodSource("condicionesUrgentesDos")
    void clasificar_debeRetornarUrgenteDosEnLosLimitesDefinidos(Consumer<Paciente> condicion) {
        Paciente paciente = pacienteEstable();
        condicion.accept(paciente);

        assertEquals(NivelTriage.URGENTE_2, paciente.clasificar());
    }

    private static Stream<Arguments> condicionesUrgentesDos() {
        return Stream.of(
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setFrecuenciaCardiaca(90)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setFrecuenciaCardiaca(120)),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setTemperatura(new BigDecimal("38.0"))),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setTemperatura(new BigDecimal("38.49"))),
                Arguments.of((Consumer<Paciente>) paciente -> paciente.setDolor(7))
        );
    }

    @Test
    void clasificar_debeDarPrioridadACriticoSobreUnaReglaDeMenorNivel() {
        Paciente paciente = pacienteEstable();
        paciente.setFrecuenciaCardiaca(121);
        paciente.setDolor(10);

        assertEquals(NivelTriage.CRITICO, paciente.clasificar());
    }

    @Test
    void clasificar_debeRetornarNoUrgenteSiNoCumpleNivelesUnoATres() {
        assertEquals(NivelTriage.NO_URGENTE, pacienteEstable().clasificar());
    }

    @Test
    void clasificar_debeRetornarNoUrgenteDosParaQuejaAdministrativaSinCriteriosMayores() {
        Paciente paciente = pacienteEstable();
        paciente.setQuejaPrincipal("  VACUNACIÓN  ");

        assertEquals(NivelTriage.NO_URGENTE_2, paciente.clasificar());
    }

    @ParameterizedTest
    @MethodSource("transicionesValidas")
    void esTransicionValida_debeAceptarLasTransicionesPermitidas(
            EstadoPaciente estadoActual, EstadoPaciente nuevoEstado) {
        Paciente paciente = pacienteEstable();
        paciente.setEstado(estadoActual);

        assertTrue(paciente.esTransicionValida(nuevoEstado));
    }

    private static Stream<Arguments> transicionesValidas() {
        return Stream.of(
                Arguments.of(EstadoPaciente.REGISTRADO, EstadoPaciente.EN_CONSULTA),
                Arguments.of(EstadoPaciente.REGISTRADO, EstadoPaciente.ALTA),
                Arguments.of(EstadoPaciente.EN_CONSULTA, EstadoPaciente.ALTA)
        );
    }

    @ParameterizedTest
    @MethodSource("transicionesInvalidas")
    void esTransicionValida_debeRechazarLasTransicionesNoPermitidas(
            EstadoPaciente estadoActual, EstadoPaciente nuevoEstado) {
        Paciente paciente = pacienteEstable();
        paciente.setEstado(estadoActual);

        assertFalse(paciente.esTransicionValida(nuevoEstado));
    }

    private static Stream<Arguments> transicionesInvalidas() {
        return Stream.of(
                Arguments.of(EstadoPaciente.REGISTRADO, EstadoPaciente.REGISTRADO),
                Arguments.of(EstadoPaciente.EN_CONSULTA, EstadoPaciente.REGISTRADO),
                Arguments.of(EstadoPaciente.EN_CONSULTA, EstadoPaciente.EN_CONSULTA),
                Arguments.of(EstadoPaciente.ALTA, EstadoPaciente.REGISTRADO),
                Arguments.of(EstadoPaciente.ALTA, EstadoPaciente.EN_CONSULTA),
                Arguments.of(EstadoPaciente.ALTA, EstadoPaciente.ALTA)
        );
    }

    private static Paciente pacienteEstable() {
        return Paciente.builder()
                .nombreCompleto("Paciente de prueba")
                .edad(30)
                .frecuenciaCardiaca(75)
                .presionSistolica(120)
                .presionDiastolica(80)
                .spo2(98)
                .temperatura(new BigDecimal("37.0"))
                .dolor(2)
                .gcs(15)
                .quejaPrincipal("dolor de cabeza")
                .build();
    }
}
