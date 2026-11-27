package com.vidacruz.triage.infraestructura.adaptador.in.web.exception;

import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errores = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (a, b) -> a
                ));
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validación fallida",
                "camposInvalidos", errores
        ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity handleValidation(WebExchangeBindException ex) {
        Map<String, String> errores = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (a, b) -> a
                ));
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validación fallida",
                "camposInvalidos", errores
        ));
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity handleTransicionInvalida(TransicionEstadoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", "Transición de estado inválida",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(PacienteNoEncontradoException.class)
    public ResponseEntity handleNoEncontrado(PacienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Recurso no encontrado",
                "message", ex.getMessage()
        ));
    }
}
