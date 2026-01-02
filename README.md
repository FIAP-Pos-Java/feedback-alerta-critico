Sistema de Feedback - Alerta Crítico

Sistema serverless que recebe feedbacks de alunos, envia alertas quando críticos e faz relatórios semanais.

## O que faz
1. Recebe avaliações - endpoint REST com descrição e nota (0-10)
2. Envia alertas - nota ≤ 3 manda email via SES
3. Gera relatórios - calcula média semanal no CloudWatch

## Como funciona
Cliente faz POST /avaliacao, que salva no DynamoDB.
Se nota for ≤ 3, publica no SNS, lambda processa e envia email via SES.

EventBridge roda semanalmente, lambda busca no DynamoDB, calcula média e faz log no CloudWatch.

## Tecnologias
Java 21, Quarkus 3.30.5, AWS Lambda, DynamoDB, SNS, SES, EventBridge, CloudWatch

## Como rodar
./mvnw quarkus:dev

Roda em http://localhost:8080

## Testar endpoint
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao": "Aula muito boa!", "nota": 9}'

Mais exemplos no CURL_EXAMPLES.md

## Configuração
Configs no application.properties ou variáveis de ambiente:
- AWS_REGION (padrão: us-east-1)
- DYNAMODB_TABLE_NAME (padrão: feedbacks)
- SNS_TOPIC_ARN
- SES_EMAIL_DESTINO (padrão: eduardaclx@gmail.com)
- SES_EMAIL_REMETENTE (verificado no SES)

## Na AWS precisa
1. DynamoDB: tabela "feedbacks" com partition key "id" e sort key "dataCriacao"
2. SNS: tópico com subscription para lambda de alertas
3. SES: verificar emails remetente e destino
4. EventBridge: cron semanal para relatório
5. IAM: permissões para DynamoDB, SNS, SES, CloudWatch

## Build
./mvnw clean package

JAR fica em target/feedback-1.0.0-SNAPSHOT-runner.jar

## Dados na tabela DynamoDB (feedbacks)
- id (String) - Partition Key
- dataCriacao (String) - Sort Key
- descricao (String)
- nota (Number, 0-10)
- critico (Boolean)

## Endpoint POST /avaliacao
Envia: {"descricao": "Aula excelente!", "nota": 9}
Recebe: {"id": "uuid-aqui", "mensagem": "Avaliação registrada com sucesso"}

Código segue boas práticas, validação, tratamento de erros e logs estruturados.

## Problemas?
- Verifica variáveis de ambiente
- Permissões IAM corretas
- Serviços AWS criados
- Logs no CloudWatch
