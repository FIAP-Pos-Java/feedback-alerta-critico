package com.feedback.lambda;

import com.feedback.model.Feedback;
import com.feedback.service.AlertaService;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AlertaLambda {

    private static final Logger LOG = Logger.getLogger(AlertaLambda.class);

    @Inject
    AlertaService alertaService;

    @Funq
    public void processarAlertaCritico(Feedback feedback) {
        try {
            if (feedback == null) {
                LOG.error("Feedback nulo recebido");
                return;
            }

            LOG.infof("Processando alerta critico. ID: %s, Nota: %d", feedback.getId(), feedback.getNota());
            alertaService.enviarAlerta(feedback);

        } 
        catch (Exception e) 
        {
            LOG.errorf(e, "Erro ao processar alerta critico");
            throw new RuntimeException("Erro ao processar alerta", e);
        }
    }
}

