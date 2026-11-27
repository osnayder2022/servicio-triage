package com.vidacruz.triage.infraestructura.adaptador.in.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.AssertTrue;

public record RegistroPacienteRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres")
        String nombreCompleto,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 0, message = "La edad debe ser mayor o igual a 0")
        @Max(value = 120, message = "La edad no puede superar 120 años")
        Integer edad,

        @NotNull(message = "La frecuencia cardíaca es obligatoria")
        @Min(value = 20, message = "La frecuencia cardíaca debe ser al menos 20 bpm")
        @Max(value = 250, message = "La frecuencia cardíaca no puede superar 250 bpm")
        Integer frecuenciaCardiaca,

        @NotNull(message = "La presión sistólica es obligatoria")
        @Min(value = 60, message = "La presión sistólica debe ser al menos 60 mmHg")
        @Max(value = 260, message = "La presión sistólica no puede superar 260 mmHg")
        Integer presionSistolica,

        @NotNull(message = "La presión diastólica es obligatoria")
        @Min(value = 30, message = "La presión diastólica debe ser al menos 30 mmHg")
        @Max(value = 160, message = "La presión diastólica no puede superar 160 mmHg")
        Integer presionDiastolica,

        @NotNull(message = "La saturación SpO2 es obligatoria")
        @Min(value = 70, message = "SpO2 debe estar entre 70% y 100%")
        @Max(value = 100, message = "SpO2 debe estar entre 70% y 100%")
        Integer spo2,

        @NotNull(message = "La temperatura es obligatoria")
        @DecimalMin(value = "34.0", message = "La temperatura debe ser al menos 34.0 °C")
        @DecimalMax(value = "42.0", message = "La temperatura no puede superar 42.0 °C")
        BigDecimal temperatura,

        @NotNull(message = "El nivel de dolor es obligatorio")
        @Min(value = 0, message = "El dolor debe estar entre 0 y 10")
        @Max(value = 10, message = "El dolor debe estar entre 0 y 10")
        Integer dolor,

        @NotNull(message = "La escala de Glasgow (GCS) es obligatoria")
        @Min(value = 3, message = "GCS debe estar entre 3 y 15")
        @Max(value = 15, message = "GCS debe estar entre 3 y 15")
        Integer gcs,

        @NotBlank(message = "La queja principal es obligatoria")
        String quejaPrincipal
) {
    @AssertTrue(message = "La presion diastolica no puede superar la sistolica")
    public boolean isPresionArterialValida() {
        return presionSistolica == null || presionDiastolica == null || presionDiastolica < presionSistolica;
    }
}
