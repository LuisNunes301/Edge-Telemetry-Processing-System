# Edge Telemetry Processing System

Pipeline distribuido de alta performance para ingestao, processamento assincrono e persistencia de telemetria de dispositivos IoT, construido com Java 21, Spring Boot, Kafka, PostgreSQL e Arquitetura Hexagonal.

---

## Arquitetura do Sistema

O fluxo de dados desacopla a ingestao HTTP da persistencia em banco de dados atraves de um mensageiro distribuido, garantindo resiliencia e escalabilidade.

```text
  +------------------+         HTTP POST         +-------------------------+
  |  Python IoT      | ------------------------> | Spring Boot IoT Gateway |
  |  Simulator       |                           | (REST / Port 8080)      |
  +------------------+                           +-------------------------+
                                                             |
                                                             | Publishes JSON
                                                             v
                                                 +-------------------------+
                                                 |      Apache Kafka       |
                                                 |  Topic: telemetry.ingestion
                                                 +-------------------------+
                                                             |
                                                             | Consumes Stream
                                                             v
  +------------------+         JPA / JDBC        +-------------------------+
  |    PostgreSQL    | <------------------------ | Spring Boot Processor   |
  |  (telemetry_db)  |                           | (Hexagonal Architecture)|
  +------------------+                           +-------------------------+

```

---

## Design Pattern: Arquitetura Hexagonal (Processor)

O microsservico `telemetryProcessor` isola o dominio de negocio de frameworks externos e infraestrutura:

```text
src/main/java/com/api/telemetryProcessor/
├── domain/                    # Core de Negocio (Entidades e Portas)
│   ├── entity/                # TelemetryEvent (Record)
│   └── port/                  # Interfaces (inbound/outbound)
├── application/               # Casos de Uso (Regras de Negocio)
│   └── usecase/               # ProcessAndPersistTelemetryUseCase
└── infraestructure/           # Adaptadores de Tecnologia
    ├── in/kafka/              # Kafka Consumer (@KafkaListener)
    └── out/database/          # Spring Data JPA & PostgreSQL Adapters

```

---

## Seguranca de Borda (Edge Security)

Para proteger a entrada do pipeline contra acessos nao autorizados, o Device Gateway exige autenticacao baseada em API Key enviada via cabecalho HTTP (`X-API-Key`) em todas as requisicoes de ingestao de telemetria (`/api/telemetry`).

* Cabecalho Requerido: `X-API-Key: sua-chave-configurada`
* Configuracao: Gerenciada via variavel de ambiente `GATEWAY_API_KEY` mapeada no `.env` e injetada no container.

---

## Como Executar o Projeto

### 1. Pre-requisitos

* Docker & Docker Compose instalados.
* Python 3.x (caso execute o simulador fora do container).

### 2. Subir a Infraestrutura e os Microsservicos

Na raiz do projeto, execute o comando para construir e iniciar os containers (incluindo o enxame de dispositivos simulados):

```bash
docker compose up --build -d

```

### 3. Validar a Persistencia no Banco

Verifique se os dados enviados pelos dispositivos foram processados e gravados no PostgreSQL:

```bash
docker exec -it telemetry-db psql -U postgres -d telemetry_db -c "SELECT device_id, temperature, humidity, event_timestamp FROM telemetry_events;"

```

---

## JSON Esperado do IoT

Exemplo de payload aceito pelo Gateway (com validacao de API Key):

```json
{
  "deviceId": "TRK-CAM-092",
  "timestamp": "2026-09-13T20:01:17.183959Z",
  "location": {
    "latitude": -12.697313,
    "longitude": -38.32404
  },
  "metrics": {
    "temperature": -0.0,
    "humidity": 51.3,
    "batteryLevel": 99.9
  },
  "metadata": {
    "firmwareVersion": "1.2.4",
    "networkType": "4G"
  }
}

```

---

## Metricas e Observabilidade

O pipeline conta com uma stack de monitoramento nativa integrada via Micrometer, Spring Boot Actuator, Prometheus e Grafana, permitindo o acompanhamento em tempo real da saude, vazao e latencia dos microsservicos.

### Principais Metricas Expostas

| Metrica (PromQL) | Tipo | Descricao / Objetivo |
| --- | --- | --- |
| `telemetry_processed_success_total` | Contador Customizado | Total de eventos de telemetria processados e salvos com sucesso pelo `telemetryProcessor`. |
| `telemetry_processed_errors_total` | Contador Customizado | Total de falhas ou excecoes ocorridas durante o pipeline de persistencia. |
| `http_server_requests_seconds_count` | Histograma (Micrometer) | Taxa total de requisicoes HTTP recebidas pelo `deviceGateway` (RED Method - Request Rate). |
| `http_server_requests_seconds_bucket` | Histograma (Micrometer) | Distribuicao de latencia de resposta do Gateway para calculo de percentis (ex: P95). |

### Acessando os Dashboards

1. Prometheus (Targets & Alvos): Acesse `http://localhost:9090/targets` para validar se o `gateway` e o `processor` estao ativos e respondendo na aba de escuta de metricas.
2. Grafana (Visualizacao): Acesse `http://localhost:3000` (com credenciais configuradas no `.env`) para construir o dashboard utilizando o Prometheus como Data Source.

Recomenda-se estruturar paineis com as seguintes queries do PromQL:

* Taxa de Sucesso (Processador):
```promql
rate(telemetry_processed_success_total[1m])

```


* Erros no Pipeline:
```promql
rate(telemetry_processed_errors_total[1m])

```


* Trafego HTTP na Borda (Gateway RPS):
```promql
sum(rate(http_server_requests_seconds_count[1m]))

```


* Latencia P95 do Gateway:
```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[1m])) by (le))

```



---

## Stack Tecnologica

* Core: Java 21, Spring Boot 3.3.3
* Mensageria: Apache Kafka (Confluent Platform 7.7.0)
* Banco de Dados: PostgreSQL 15
* Infraestrutura: Docker Compose (Rede Bridge customizada)
* Observabilidade: Grafana e Prometheus
* Resiliencia & Testes: Dead Letter Queue (DLQ) & Testcontainers (JUnit 5)


