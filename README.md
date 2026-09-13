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

---

## 🛠️ Stack Tecnológica

* **Core:** Java 21, Spring Boot 3.3.3
* **Mensageria:** Apache Kafka (Confluent Platform 7.7.0)
* **Banco de Dados:** PostgreSQL 15
* **Infraestrutura:** Docker Compose (Rede Bridge customizada)