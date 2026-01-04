package com.feedback.lambda;

import com.feedback.dto.SnsEventWrapper;
import com.feedback.model.Feedback;
import com.feedback.service.AlertaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Map;

@ApplicationScoped
public class AlertaLambda {

    private static final Logger LOG = Logger.getLogger(AlertaLambda.class);

    @Inject
    AlertaService alertaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * O Quarkus Funqy faz o unwrap automático do evento SNS!
     * Ele pega o campo "Message" do SNS e deserializa automaticamente para Feedback.
     * Por isso recebemos diretamente o objeto Feedback aqui.
     */
    @Funq
    public void processarAlertaCritico(Feedback feedback) {
        try {
            LOG.info("=== INICIO PROCESSAMENTO ALERTA CRITICO ===");
            
            if (feedback == null) {
                LOG.error("Feedback é nulo!");
                return;
            }

            LOG.infof("Feedback crítico recebido via SNS:");
            LOG.infof("  - ID: %s", feedback.getId());
            LOG.infof("  - Descrição: %s", feedback.getDescricao());
            LOG.infof("  - Nota: %d", feedback.getNota());
            LOG.infof("  - Crítico: %s", feedback.getCritico());
            LOG.infof("  - Data: %s", feedback.getDataCriacao());
            
            alertaService.enviarAlerta(feedback);

            LOG.info("✅ Alerta processado e email enviado com sucesso!");
            LOG.info("=== FIM PROCESSAMENTO ALERTA CRITICO ===");
            
        } catch (Exception e) {
            LOG.errorf(e, "❌ Erro ao processar alerta crítico do SNS");
            throw new RuntimeException("Erro ao processar alerta", e);
        }
    }
}

