package com.vidacruz.triage.infraestructura.adaptador.in.web.controlador;

import com.vidacruz.triage.aplicacion.casouso.port.in.GestionarEstadoPacienteCasoUso;
import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import com.vidacruz.triage.dominio.entidades.EstadoPaciente;
import com.vidacruz.triage.dominio.entidades.NivelTriage;
import com.vidacruz.triage.dominio.entidades.RegistroPacienteResponse;
import com.vidacruz.triage.infraestructura.adaptador.in.web.exceptionhandler.GlobalExceptionHandler;
import com.vidacruz.triage.infraestructura.config.MapperConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest(TriageControlador.class)
@Import({MapperConfig.class, GlobalExceptionHandler.class})
class TriageControladorTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegistrarPacienteCasoUso registrarPacienteCasoUso;

    @MockBean
    private GestionarEstadoPacienteCasoUso gestionarEstadoPacienteCasoUso;

    @Test
    void registrarPaciente_debeAceptarLosLimitesValidosDeRf01() {
        given(registrarPacienteCasoUso.registrar(any())).willReturn(Mono.just(respuestaRegistro()));

        webTestClient.post().uri("/triage/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitudValida())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nivelClasificado").isEqualTo("NO_URGENTE");
    }

    @ParameterizedTest
    @MethodSource("solicitudesInvalidasRf01")
    void registrarPaciente_debeRechazarValoresFueraDelContratoRf01(Consumer<Map<String, Object>> mutacion) {
        Map<String, Object> solicitud = solicitudValida();
        mutacion.accept(solicitud);

        webTestClient.post().uri("/triage/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitud)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.camposInvalidos").exists();
    }

    private static Stream<Arguments> solicitudesInvalidasRf01() {
        return Stream.of(
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("nombreCompleto", "Al")),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("nombreCompleto", "x".repeat(101))),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("edad", 121)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("frecuenciaCardiaca", 19)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("frecuenciaCardiaca", 251)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("presionSistolica", 59)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("presionSistolica", 261)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("presionDiastolica", 29)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("presionDiastolica", 161)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("presionDiastolica", 120)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("spo2", 69)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("spo2", 101)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("temperatura", 33.9)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("temperatura", 42.1)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("dolor", -1)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("dolor", 11)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("gcs", 2)),
                Arguments.of((Consumer<Map<String, Object>>) json -> json.put("gcs", 16))
        );
    }

    private static Map<String, Object> solicitudValida() {
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("nombreCompleto", "Ana");
        solicitud.put("edad", 0);
        solicitud.put("frecuenciaCardiaca", 20);
        solicitud.put("presionSistolica", 60);
        solicitud.put("presionDiastolica", 30);
        solicitud.put("spo2", 70);
        solicitud.put("temperatura", 34.0);
        solicitud.put("dolor", 0);
        solicitud.put("gcs", 3);
        solicitud.put("quejaPrincipal", "Dolor");
        return solicitud;
    }

    private static RegistroPacienteResponse respuestaRegistro() {
        return new RegistroPacienteResponse(1L, NivelTriage.NO_URGENTE, 60,
                EstadoPaciente.REGISTRADO, Instant.parse("2026-01-01T00:00:00Z"));
    }
}
