# Sistema de Feedback - AWS Lambda com Quarkus

É um sistema serverless que recebe feedbacks de alunos, envia alertas quando são críticos e gera relatórios semanais.

## O que faz?

O sistema tem 3 funções principais:

1. **Receber avaliações** - Endpoint REST que recebe feedbacks com descrição e nota (0 a 10)
2. **Enviar alertas** - Quando uma nota é ≤ 3, envia email automático via SES
3. **Gerar relatórios** - Toda semana calcula a média das notas e gera um log no CloudWatch

## Como funciona?

```
Cliente → POST /avaliacao → Salva no DynamoDB
                              ↓
                         Se nota ≤ 3
                              ↓
                         Publica no SNS
                              ↓
                         Lambda processa
                              ↓
                         Envia email via SES

EventBridge (cron semanal) → Lambda → Busca no DynamoDB → Calcula média → Log no CloudWatch
```

## Tecnologias

- Java 21
- Quarkus 3.30.5
- AWS Lambda
- DynamoDB (banco de dados)
- SNS (mensageria)
- SES (envio de email)
- EventBridge (agendamento)
- CloudWatch (logs)

## Como rodar localmente?

```bash
./mvnw quarkus:dev
```

A aplicação vai rodar em `http://localhost:8080`

### Testar o endpoint

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula muito boa!",
    "nota": 9
  }'
```

**Mais exemplos de requisições:** Veja o arquivo [CURL_EXAMPLES.md](CURL_EXAMPLES.md) com vários exemplos de curl para testar no Postman ou terminal.

## Estrutura do projeto

```
src/main/java/com/feedback/
├── config/          # Configurações AWS
├── dto/             # Objetos de transferência de dados
├── exception/       # Exceções customizadas
├── lambda/          # Handlers das Lambdas
├── model/           # Entidades (Feedback)
├── repository/      # Acesso ao DynamoDB
├── resource/        # Endpoint REST
└── service/         # Lógica de negócio
```

## Configuração

As configurações ficam no `application.properties` ou podem ser passadas como variáveis de ambiente:

- `AWS_REGION` - Região AWS (padrão: us-east-1)
- `DYNAMODB_TABLE_NAME` - Nome da tabela (padrão: feedbacks)
- `SNS_TOPIC_ARN` - ARN do tópico SNS
- `SES_EMAIL_DESTINO` - Email que recebe os alertas (padrão: eduardaclx@gmail.com)
- `SES_EMAIL_REMETENTE` - Email remetente (precisa estar verificado no SES)

## O que precisa na AWS?

1. **DynamoDB**: Criar tabela `feedbacks` com:
   - Partition Key: `id` (String)
   - Sort Key: `dataCriacao` (String)

2. **SNS**: Criar tópico e configurar subscription para a Lambda de alertas

3. **SES**: Verificar email remetente e destino

4. **EventBridge**: Configurar regra de cron para executar a Lambda de relatório semanalmente

5. **IAM**: Dar permissões para as Lambdas acessarem DynamoDB, SNS, SES e CloudWatch

## Build

```bash
./mvnw clean package
```

O JAR vai ficar em `target/feedback-1.0.0-SNAPSHOT-runner.jar`

## Deploy

Pode fazer upload do JAR na AWS Console ou usar SAM/Serverless Framework. Cada Lambda precisa ser configurada separadamente:

- **AvaliacaoResource**: Configurar como Function URL ou API Gateway
- **AlertaLambda**: Handler `io.quarkus.funqy.lambda.FunqyStreamHandler::handleRequest`
- **RelatorioLambda**: Handler `io.quarkus.funqy.lambda.FunqyStreamHandler::handleRequest`

## Estrutura de dados

### Tabela DynamoDB (feedbacks)

- `id` (String) - Partition Key
- `dataCriacao` (String) - Sort Key
- `descricao` (String)
- `nota` (Number, 0-10)
- `critico` (Boolean)

### Endpoint POST /avaliacao

**Request:**
```json
{
  "descricao": "Aula excelente!",
  "nota": 9
}
```

**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "mensagem": "Avaliação registrada com sucesso"
}
```

## Observações

- O código segue princípios de responsabilidade única (cada classe faz uma coisa só)
- Validação usando Bean Validation
- Tratamento de erros com respostas HTTP adequadas
- Logs estruturados no CloudWatch

## Dúvidas?

Se tiver algum problema, verifica:
- Se as variáveis de ambiente estão configuradas
- Se as permissões IAM estão corretas
- Se os serviços AWS estão criados (DynamoDB, SNS, SES)
- Os logs no CloudWatch para ver erros
