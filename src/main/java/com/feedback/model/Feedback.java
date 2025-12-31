package com.feedback.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@DynamoDbBean
public class Feedback {

    private String id;
    private String descricao;
    private Integer nota;
    private String dataCriacao;
    private Boolean critico;

    public Feedback() {
    }

    public Feedback(String id, String descricao, Integer nota, String dataCriacao, Boolean critico) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.dataCriacao = dataCriacao;
        this.critico = critico;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("descricao")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @DynamoDbAttribute("nota")
    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("dataCriacao")
    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @DynamoDbAttribute("critico")
    public Boolean getCritico() {
        return critico;
    }

    public void setCritico(Boolean critico) {
        this.critico = critico;
    }

    public static Feedback fromRequest(String descricao, Integer nota) {
        String id = java.util.UUID.randomUUID().toString();
        String dataCriacao = Instant.now().toString();
        Boolean critico = nota <= 3;
        
        return new Feedback(id, descricao, nota, dataCriacao, critico);
    }
}

