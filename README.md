# Lycan

**AI-Native Asset Intelligence Engine**

Lycan is an on-premise **AI-powered asset intelligence system** that transforms raw file storage into a **semantic knowledge base**.

Built with modern Java and vector search technology, Lycan ingests documents, generates embeddings, and enables natural-language querying through a REST API and interactive web interface.

Instead of relying on filenames or folder structures, Lycan allows users to ask questions about their data and retrieve answers grounded in the actual document contents.

---

# Core Capabilities

## Semantic Asset Discovery

Lycan uses vector embeddings to perform semantic search across ingested files.

Users can retrieve relevant documents using natural language queries such as:

```
"Show me the contract about the office lease"
```

—even if the document name is unrelated.

---

## Retrieval-Augmented Generation (RAG)

Lycan combines vector search with large language models to produce **context-aware answers**.

The system workflow:

1. Retrieve the most relevant document fragments from the vector store.
2. Inject those fragments into the LLM prompt.
3. Generate a grounded response using the retrieved context.

This enables users to interact with their data as a **question-answering knowledge system**.

---

## Hardware-Aware Retrieval Engine

Lycan dynamically adjusts retrieval depth based on system resources.

The **Hardware Aware Algorithm (HAA)** monitors available memory and determines how many document vectors should be retrieved.

| System Profile | Top-K Retrieval |
| -------------- | --------------- |
| Low Memory     | 2               |
| Standard       | 5               |
| High Memory    | 10              |

This keeps the system efficient across different machines while maintaining relevant search results.

---

## REST API Interface

Lycan exposes a simple REST API for querying the system.

Example request:

```
POST /api/v1/ask
```

```
{
  "query": "Explain the Lycan architecture"
}
```

Example response:

```
{
  "answer": "Lycan is an AI-native asset intelligence engine that combines vector search and LLM orchestration..."
}
```

---

## Interactive Web Console

Lycan includes a browser-based console that allows users to interact with the system conversationally.

Features include:

* Natural language querying
* Chat-style interface
* Real-time responses from the AI engine
* System telemetry and status monitoring

---

# Technical Architecture

## Engine

* Java 21
* Multi-threaded ingestion pipeline
* High-performance file scanning

## AI Layer

* Spring AI orchestration
* Local LLM inference via Ollama
* Semantic retrieval using pgvector

## Data Layer

* PostgreSQL vector storage
* JPA / Hibernate persistence
* HikariCP connection pooling

## Infrastructure

* Dockerized services
* Local LLM execution
* On-premise deployment capability

---

# Example Workflow

```
User Query
    │
    ▼
REST API
    │
    ▼
Vector Retrieval (pgvector)
    │
    ▼
Relevant Document Context
    │
    ▼
LLM Generation
    │
    ▼
Grounded Response
```

---

# Running Lycan

Start infrastructure:

```
docker compose up
```

Run the application:

```
mvn spring-boot:run
```

Open the web interface:

```
http://localhost:8080/lycan.html
```

---

# Project Status

Current development focuses on expanding Lycan into a full **AI-powered asset intelligence platform**.

---

# Roadmap

## Completed

* Multi-threaded ingestion pipeline
* PostgreSQL persistence layer
* Vector storage with pgvector
* Spring AI RAG pipeline
* REST API interface
* Web-based chat console

## In Progress

* Hardware-aware retrieval tuning
* Streaming LLM responses
* Live system telemetry and log viewing

## Planned

* Advanced document ingestion pipeline
* Metadata enrichment using LLMs
* Visual RAG debugging interface
* Enterprise deployment profiles

---

# Vision

Lycan bridges traditional infrastructure engineering with modern AI systems, enabling organizations to transform static storage into an intelligent, queryable knowledge platform.
