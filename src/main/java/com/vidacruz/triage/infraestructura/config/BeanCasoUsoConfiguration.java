package com.vidacruz.triage.infraestructura.config;

import com.vidacruz.triage.aplicacion.service.GestionarEstadoPacienteServiceImpl;
import com.vidacruz.triage.aplicacion.casouso.port.in.GestionarEstadoPacienteCasoUso;
import com.vidacruz.triage.aplicacion.casouso.port.in.RegistrarPacienteCasoUso;
import com.vidacruz.triage.dominio.port.out.AuditoriaTriageRepositorioPort;
import com.vidacruz.triage.aplicacion.service.TriageServiceImpl;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.vidacruz.triage.dominio.port.out.PacienteRepositorioPort;
import com.vidacruz.triage.dominio.port.out.GestorMensajePort;
import org.springframework.context.annotation.Configuration;
import com.vidacruz.triage.dominio.port.out.SlaTriagePort;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;

@Configuration
public class BeanCasoUsoConfiguration {

    @Bean
    public RegistrarPacienteCasoUso registrarPacienteCasoUso(
            AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort,
            PacienteRepositorioPort pacienteRepositorioPort,
            TransactionalOperator transactionalOperator,
            GestorMensajePort gestorMensajePort,
            SlaTriagePort slaTriagePort) {
        return new TriageServiceImpl(
                auditoriaTriageRepositorioPort,
                pacienteRepositorioPort,
                transactionalOperator,
                gestorMensajePort,
                slaTriagePort);
    }

    @Bean
    public GestionarEstadoPacienteCasoUso gestionarEstadoPacienteCasoUso(
            AuditoriaTriageRepositorioPort auditoriaTriageRepositorioPort,
            PacienteRepositorioPort pacienteRepositorioPort,
            TransactionalOperator transactionalOperator,
            GestorMensajePort gestorMensajePort,
            ModelMapper mapper) {
        return new GestionarEstadoPacienteServiceImpl(
                auditoriaTriageRepositorioPort,
                pacienteRepositorioPort,
                transactionalOperator,
                gestorMensajePort,
                mapper);
    }
}
