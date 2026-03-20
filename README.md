# Lycan  
**Local-First AI Asset Intelligence Engine**

Lycan is an on-premise AI system that transforms raw files into a **queryable knowledge layer** using vector search and local LLM inference.

It is designed as a **deterministic data pipeline**, where documents are ingested, chunked, embedded, and stored for semantic retrieval, enabling natural-language interaction with local data.

---

## What I Built

Lycan is not just a chatbot, it is a **retrieval-backed intelligence system**:

- Ingests local files and converts them into vector embeddings  
- Stores document chunks in PostgreSQL (pgvector)  
- Retrieves relevant context based on semantic similarity  
- Generates grounded answers using a local LLM (Ollama)  
- Exposes the system through a REST API and web interface  

---

## System Design

### Ingestion Pipeline
- Reads files from disk and converts them into structured documents  
- Uses token-based chunking for consistent segmentation  
- Attaches metadata (source path, chunk index) for traceability  
- Designed for **idempotent processing** (deterministic chunking + planned deduplication)

---

### Retrieval-Augmented Generation (RAG)

Lycan uses a standard RAG pipeline:

1. Retrieve relevant document chunks from the vector store  
2. Inject context into the LLM prompt  
3. Generate a grounded response  

This allows users to query their data without relying on filenames or folder structure.

---

### Hardware-Aware Retrieval

Lycan dynamically adjusts retrieval size based on system resources:

| Profile       | Top-K |
|--------------|------|
| Low Memory   | 2    |
| Standard     | 5    |
| High Memory  | 10   |

This keeps inference efficient across different machines.

---

### Local-First Architecture

- Runs fully on local infrastructure  
- Uses Docker for PostgreSQL + pgvector  
- Uses Ollama for local LLM inference  
- No external API dependency  

---

## API Example

```json
POST /api/v1/ask

{
  "query": "Explain the Lycan architecture"
}
