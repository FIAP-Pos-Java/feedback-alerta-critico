package com.feedback.resource;

import com.feedback.dto.SnsEvent;
import com.feedback.lambda.AlertaLambda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/sns-webhook")
public class SnsWebhookResource {

    private static final Logger LOG = Logger.getLogger(SnsWebhookResource.class);

    @Inject
    AlertaLambda alertaLambda;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receberNotificacaoSNS(String body) {
        try {
            LOG.info("Recebida notificação SNS via webhook");
            LOG.debugf("Body recebido: %s", body);

            JsonNode rootNode = objectMapper.readTree(body);
            
            // Verifica se é uma confirmação de subscrição
            if (rootNode.has("Type") && "SubscriptionConfirmation".equals(rootNode.get("Type").asText())) {
                LOG.info("Confirmação de subscrição SNS recebida");
                return Response.ok().build();
            }

            // Extrai a mensagem SNS
            String messageStr = rootNode.has("Message") ? rootNode.get("Message").asText() : body;
            String subject = rootNode.has("Subject") ? rootNode.get("Subject").asText() : "Alerta Crítico";

            // Cria evento SNS
            SnsEvent snsEvent = new SnsEvent();
            snsEvent.setMessage(messageStr);
            snsEvent.setSubject(subject);

            // Processa através do Lambda
            alertaLambda.processarAlertaCritico(snsEvent);

            return Response.ok().entity("{\"status\": \"Notificação processada com sucesso\"}").build();
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao processar notificação SNS");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
}

