package com.api.telemetryProcessor.infraestructure.in.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {


    @Bean
    public DefaultErrorHandler errorHandler(KafkaOperations
    <Object,Object> tempate){
        // Vai tentar processar 3 vezes, aguardando 2 segundos a cada tentativa
        FixedBackOff fixedBackOff = new FixedBackOff(200L,3L);
        // Configura o recoverer para enviar a mensagem falha para o tópico
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition()));
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
