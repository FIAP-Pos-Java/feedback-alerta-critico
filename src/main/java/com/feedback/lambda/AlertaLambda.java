package com.feedback.lambda;

import com.feedback.dto.SnsEvent;
import com.feedback.model.Feedback;
import com.feedback.service.AlertaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.funqy.Funq;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

public class AlertaLambda {

    private static final Logger LOG = Logger.getLogger(AlertaLambda.class);

    @Inject
    AlertaService alertaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Funq
    public void processarAlertaCritico(SnsEvent snsEvent) {
        try {
            LOG.infof("Evento SNS recebido. Subject: %s", snsEvent.getSubject());
            String mensagem = snsEvent.getMessage();
            if (mensagem == null || mensagem.isEmpty()) {
                LOG.warn("Mensagem SNS vazia. Ignorando evento.");
                return;
            }

            Feedback feedback = objectMapper.readValue(mensagem, Feedback.class);
            LOG.infof("Feedback crítico processado. ID: %s, Nota: %d", 
                feedback.getId(), feedback.getNota());
            alertaService.enviarAlerta(feedback);

            LOG.info("Alerta processado com sucesso");
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao processar alerta crítico do SNS");
            throw new RuntimeException("Erro ao processar alerta", e);
        }
    }
}

