package com.feedback.service;

import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class AlertaService {

    private static final Logger LOG = Logger.getLogger(AlertaService.class);

    @Inject
    SesClient sesClient;

    @ConfigProperty(name = "ses.email.destino", defaultValue = "eduardaclx@gmail.com")
    String emailDestino;

    @ConfigProperty(name = "ses.email.remetente")
    String emailRemetente;

    public void enviarAlerta(Feedback feedback) {
        if (emailRemetente == null || emailRemetente.isEmpty()) {
            LOG.warn("Email remetente não configurado. Alerta não será enviado.");
            return;
        }

        try {
            String assunto = "ALERTA: Feedback Crítico Recebido";
            String corpoEmail = criarCorpoEmail(feedback);

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(emailRemetente)
                .destination(Destination.builder()
                    .toAddresses(emailDestino)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder()
                        .data(assunto)
                        .charset("UTF-8")
                        .build())
                    .body(Body.builder()
                        .text(Content.builder()
                            .data(corpoEmail)
                            .charset("UTF-8")
                            .build())
                        .build())
                    .build())
                .build();

            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            LOG.infof("Email de alerta enviado com sucesso. MessageId: %s, Feedback ID: %s", 
                response.messageId(), feedback.getId());
        } catch (SesException e) {
            LOG.errorf(e, "Erro ao enviar email de alerta. Feedback ID: %s", feedback.getId());
            throw new RuntimeException("Erro ao enviar email de alerta", e);
        }
    }

    private String criarCorpoEmail(Feedback feedback) {
        String nivelUrgencia = determinarNivelUrgencia(feedback.getNota());
        String dataFormatada = formatarData(feedback.getDataCriacao());

        return String.format(
            "ALERTA DE FEEDBACK CRÍTICO\n\n" +
            "Um feedback crítico foi recebido e requer atenção imediata.\n\n" +
            "DETALHES:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "ID: %s\n" +
            "Descrição: %s\n" +
            "Nota: %d/10\n" +
            "Nível de Urgência: %s\n" +
            "Data de Criação: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Por favor, revise este feedback e tome as ações necessárias.\n\n" +
            "Data de Envio do Alerta: %s",
            feedback.getId(),
            feedback.getDescricao(),
            feedback.getNota(),
            nivelUrgencia,
            dataFormatada,
            Instant.now().toString()
        );
    }

    private String determinarNivelUrgencia(Integer nota) {
        if (nota == 0) {
            return "CRÍTICO MÁXIMO";
        } else if (nota == 1) {
            return "CRÍTICO ALTO";
        } else if (nota == 2) {
            return "CRÍTICO";
        } else {
            return "BAIXO";
        }
    }

    private String formatarData(String dataISO) {
        try {
            Instant instant = Instant.parse(dataISO);
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .format(instant.atZone(java.time.ZoneId.systemDefault()));
        } catch (Exception e) {
            return dataISO;
        }
    }
}

