# API de Transações de Criptomoedas

API REST em Java/Spring Boot para registrar compras e vendas de BTC, ETH e SOL, consultar cotações em tempo real na Binance e manter saldos por cliente.

## Persistência de Dados

O projeto utiliza **PostgreSQL** para garantir consistência forte e transações ACID. A integridade dos saldos é assegurada por meio de:

* **Atomicidade:** As operações de transação e atualização de saldo ocorrem na mesma unidade de trabalho.
* **Precisão:** Uso do tipo `NUMERIC` para evitar erros de arredondamento em valores financeiros.
* **Concorrência:** Implementação de bloqueio pessimista (`Pessimistic Lock`) para garantir a consistência dos saldos durante acessos simultâneos.

---

## Como executar

```bash
docker compose up --build

```

A API ficará disponível em `http://localhost:8080`.

**Swagger/OpenAPI:**

```text
http://localhost:8080/swagger-ui.html

```

---

## Endpoints

### Registrar transação

`POST /transactions`

**Compra:**

```bash
curl -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{
    "clientId": "a3bc6a7d-8f0d-4d08-98c2-0b715cc19dd2",
    "type": "buy",
    "cryptocurrency": "BTC",
    "amountBRL": 1000.00
  }'

```

**Venda:**

```bash
curl -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{
    "clientId": "a3bc6a7d-8f0d-4d08-98c2-0b715cc19dd2",
    "type": "sell",
    "cryptocurrency": "BTC",
    "amountCrypto": 0.00010000
  }'

```

### Consultar saldos

`GET /clients/{clientId}/balances`

```bash
curl http://localhost:8080/clients/a3bc6a7d-8f0d-4d08-98c2-0b715cc19dd2/balances

```

---

## Regras implementadas

* **Ativos suportados:** `BTC`, `ETH`, `SOL`.
* **Cálculo de Compra:** Exige `amountBRL` e calcula `amountCrypto = amountBRL / cotação`.
* **Cálculo de Venda:** Exige `amountCrypto` e calcula `amountBRL = amountCrypto * cotação`.
* **Validação de Saldo:** Vendas sem saldo suficiente retornam `422 Unprocessable Entity`.
* **Integração Externa:** Falhas na API da Binance retornam `502 Bad Gateway`.
* **Validação de Payload:** UUID inválido ou cripto não suportada retornam `400 Bad Request`.
* **Precisão:** Quantidades de cripto utilizam 8 casas decimais; valores em BRL utilizam 2 casas.

---

## Arquitetura Hexagonal

A solução é organizada seguindo o padrão de portas e adaptadores para isolar a lógica de domínio:

```text
com.vitorvidal.criptomoedas
+-- domain
|   +-- Regras puras, enums e cálculos de precisão
+-- application
|   +-- command/result
|   +-- port.in / port.out
|   +-- service
+-- adapter.in.web
|   +-- Controllers REST, DTOs e tratamento de erros
+-- adapter.out.persistence
|   +-- Adapter Postgres/JPA, entidades e repositórios
+-- adapter.out.price.binance
|   +-- Adapter da API externa da Binance
+-- adapter.out.transaction
    +-- Adapter Spring para unidade de trabalho transacional

```

---

## Testes e Stack

**Execução de testes:**

```bash
mvn test

```

**Stack Tecnológica:**

* Java 21
* Spring Boot (Web, Data JPA)
* PostgreSQL
* Flyway (Migração de banco)
* Springdoc OpenAPI
* Docker Compose