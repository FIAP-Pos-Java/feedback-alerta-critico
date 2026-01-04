package com.feedback.service;

import com.feedback.model.Feedback;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class AlertaService {

    private static final Logger LOG = Logger.getLogger(AlertaService.class);

    @Inject
    SesClient sesClient;

    @ConfigProperty(name = "ses.email.destino")
    String emailDestino;

    @ConfigProperty(name = "ses.email.remetente")
    String emailRemetente;

    public void enviarAlerta(Feedback feedback) {
        if (emailRemetente == null || emailRemetente.isEmpty()) {
            LOG.warn("Email remetente não configurado. Alerta não será enviado.");
            return;
        }

        try {
            var assunto = "ALERTA: Feedback Crítico Recebido";
            var corpoEmail = criarCorpoEmail(feedback);

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

            sesClient.sendEmail(emailRequest);
            LOG.infof("Email enviado. Feedback ID: %s", feedback.getId());
        } 
        catch (SesException e) 
        {
            LOG.errorf(e, "Erro ao enviar email. Feedback ID: %s", feedback.getId());
            throw new RuntimeException("Erro ao enviar email de alerta", e);
        }
    }

    private String criarCorpoEmail(Feedback feedback) {
        var nivelUrgencia = determinarNivelUrgencia(feedback.getNota());
        var dataFormatada = formatarData(feedback.getDataCriacao());

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
            "Por favor, revise este feedback e tome as ações necessárias.",
            feedback.getId(),
            feedback.getDescricao(),
            feedback.getNota(),
            nivelUrgencia,
            dataFormatada
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
            var dataHora = java.time.LocalDateTime.parse(dataISO);
            var formatoBrasileiro = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return dataHora.format(formatoBrasileiro);
        } 
        catch (Exception e) 
        {
            try {
                if (dataISO != null && dataISO.contains("T")) {
                    var partes = dataISO.split("T");
                    var data = partes[0];
                    var hora = partes[1].split("\\.")[0];
                    return data.replace("-", "/") + " " + hora;
                }
            } 
            catch (Exception ex) 
            {
                LOG.warnf(ex, "Erro ao formatar data alternativa. DataISO: %s", dataISO);
            }

            return dataISO;
        }
    }
}

