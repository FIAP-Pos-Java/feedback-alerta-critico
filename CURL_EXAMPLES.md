# Exemplos de Requisições cURL

Exemplos de requisições para testar o endpoint de avaliação no Postman ou terminal.

## Endpoint Base

```
http://localhost:8080/avaliacao
```

## 1. Avaliação Normal (Nota Alta)

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula muito boa, professor explicou bem todos os conceitos!",
    "nota": 9
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "mensagem": "Avaliação registrada com sucesso"
}
```

## 2. Avaliação Crítica (Nota Baixa - Vai enviar email)

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula muito confusa, não entendi nada. Precisa melhorar a explicação.",
    "nota": 2
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "mensagem": "Avaliação registrada com sucesso"
}
```

## 3. Avaliação Média

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula ok, mas poderia ter mais exemplos práticos",
    "nota": 6
  }'
```

## 4. Avaliação Muito Crítica (Nota 0)

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Péssima aula, conteúdo desatualizado e professor não sabia explicar",
    "nota": 0
  }'
```

## 5. Erro de Validação - Nota Inválida (Maior que 10)

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula excelente!",
    "nota": 15
  }'
```

**Resposta esperada (400 Bad Request):**
```json
{
  "mensagem": "Erro de validação: A nota deve estar entre 0 e 10"
}
```

## 6. Erro de Validação - Nota Negativa

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula ruim",
    "nota": -5
  }'
```

**Resposta esperada (400 Bad Request):**
```json
{
  "mensagem": "Erro de validação: A nota deve estar entre 0 e 10"
}
```

## 7. Erro de Validação - Descrição Vazia

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "",
    "nota": 8
  }'
```

**Resposta esperada (400 Bad Request):**
```json
{
  "mensagem": "Erro de validação: A descrição é obrigatória"
}
```

## 8. Erro de Validação - Descrição Ausente

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "nota": 7
  }'
```

**Resposta esperada (400 Bad Request):**
```json
{
  "mensagem": "Erro de validação: A descrição é obrigatória"
}
```

## 9. Erro de Validação - Nota Ausente

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula boa"
  }'
```

**Resposta esperada (400 Bad Request):**
```json
{
  "mensagem": "Erro de validação: A nota é obrigatória"
}
```

## 10. Requisição com JSON Malformado

```bash
curl -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aula boa",
    "nota": 8
  '
```

**Resposta esperada (400 Bad Request):** Erro de parsing JSON

## Para usar no Postman

1. **Método:** POST
2. **URL:** `http://localhost:8080/avaliacao`
3. **Headers:**
   - `Content-Type: application/json`
4. **Body (raw JSON):**
```json
{
  "descricao": "Sua descrição aqui",
  "nota": 8
}
```

## Casos de Teste Recomendados

### Teste de Fluxo Completo

1. Enviar avaliação com nota 9 (não deve enviar email)
2. Enviar avaliação com nota 3 (deve enviar email)
3. Enviar avaliação com nota 1 (deve enviar email)
4. Verificar no DynamoDB se os dados foram salvos
5. Verificar no CloudWatch se os logs estão corretos
6. Verificar no email se os alertas foram recebidos

### Testes de Validação

- Nota fora do range (0-10)
- Descrição vazia
- Campos obrigatórios ausentes
- Tipos incorretos (string no lugar de número)

