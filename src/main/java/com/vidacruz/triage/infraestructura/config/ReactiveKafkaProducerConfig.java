package com.vidacruz.triage.infraestructura.config;

import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import reactor.kafka.sender.SenderOptions;
import com.vidacruz.triage.dominio.entidades.TriageEvent;
import java.util.Map;

@Configuration
public class ReactiveKafkaProducerConfig {

    @Bean
    public ReactiveKafkaProducerTemplate<String, TriageEvent> reactiveKafkaProducerTemplate(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProps = kafkaProperties.buildProducerProperties();
        SenderOptions<String, TriageEvent> senderOptions = SenderOptions.create(producerProps);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
