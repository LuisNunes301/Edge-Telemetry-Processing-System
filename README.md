# Edge Telemetry Processing System

Pipeline distribuído de alta performance para ingestão, processamento assíncrono e persistência de telemetria de dispositivos IoT, construído com **Java 21**, **Spring Boot**, **Kafka**, **PostgreSQL** e **Arquitetura Hexagonal**.

---

## 🏗️ Arquitetura do Sistema

O fluxo de dados desacopla a ingestão HTTP da persistência em banco de dados através de um mensageiro distribuído, garantindo resiliência e escalabilidade.

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

## 🏛️ Design Pattern: Arquitetura Hexagonal (Processor)

O microsserviço `telemetryProcessor` isola o domínio de negócio de frameworks externos e infraestrutura:

```text
src/main/java/com/api/telemetryProcessor/
├── domain/                  # Core de Negócio (Entidades e Portas)
│   ├── entity/              # TelemetryEvent (Record)
│   └── port/                # Interfaces (inbound/outbound)
├── application/             # Casos de Uso (Regras de Negócio)
│   └── usecase/             # ProcessAndPersistTelemetryUseCase
└── infraestructure/         # Adaptadores de Tecnologia
    ├── in/kafka/            # Kafka Consumer (@KafkaListener)
    └── out/database/        # Spring Data JPA & PostgreSQL Adapters

```

---

## 🚀 Como Executar o Projeto

### 1. Pré-requisitos

* Docker & Docker Compose instalados.
* Python 3.x (para o simulador).

### 2. Subir a Infraestrutura e os Microsserviços

Na raiz do projeto, execute o comando para construir e iniciar os containers:

```bash
docker compose up --build -d

```

### 3. Executar o Simulador de Dispositivos

Dispare o script Python para injetar dados de telemetria no Gateway:

```bash
python edge.py

```

### 4. Validar a Persistência no Banco

Verifique se os dados foram processados e gravados no PostgreSQL:

```bash
docker exec -it telemetry-db psql -U postgres -d telemetry_db -c "SELECT device_id, temperature, humidity, event_timestamp FROM telemetry_events;"

```
## 5. Json esperado do IOT

```bash
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

## 6. Métricas e Observabilidade

O pipeline conta com uma stack de monitoramento nativa integrada via **Micrometer**, **Spring Boot Actuator**, **Prometheus** e **Grafana**, permitindo o acompanhamento em tempo real da saúde, vazão e latência dos microsserviços.

### Principais Métricas Expostas

| Métrica (PromQL) | Tipo | Descrição / Objetivo |
| :--- | :--- | :--- |
| `telemetry_processed_success_total` | Contador Customizado | Total de eventos de telemetria processados e salvos com sucesso pelo `telemetryProcessor`. |
| `telemetry_processed_errors_total` | Contador Customizado | Total de falhas ou exceções ocorridas durante o pipeline de persistência. |
| `http_server_requests_seconds_count` | Histograma (Micrometer) | Taxa total de requisições HTTP recebidas pelo `deviceGateway` (RED Method - Request Rate). |
| `http_server_requests_seconds_bucket` | Histograma (Micrometer) | Distribuição de latência de resposta do Gateway para cálculo de percentis (ex: P95). |

### Acessando os Dashboards

1. **Prometheus (Targets & Alvos):** Acesse `http://localhost:9090/targets` para validar se o `gateway` e o `processor` estão ativos e respondendo na aba de escuta de métricas.
2. **Grafana (Visualização):** Acesse `http://localhost:3000` (com credenciais configuradas no `.env`) para construir o dashboard utilizando o **Prometheus** como Data Source. 

Recomenda-se estruturar painéis com as seguintes queries do PromQL:

* **Taxa de Sucesso (Processador):**
```promql
  rate(telemetry_processed_success_total[1m])

```

* **Erros no Pipeline:**
```promql
rate(telemetry_processed_errors_total[1m])

```


* **Tráfego HTTP na Borda (Gateway RPS):**
```promql
sum(rate(http_server_requests_seconds_count[1m]))

```


* **Latência P95 do Gateway:**
```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[1m])) by (le))

```

---

##  Stack Tecnológica

* **Core:** Java 21, Spring Boot 3.3.3
* **Mensageria:** Apache Kafka (Confluent Platform 7.7.0)
* **Banco de Dados:** PostgreSQL 15
* **Infraestrutura:** Docker Compose (Rede Bridge customizada)
* **Observabilidade:** Grafana e Prometheus