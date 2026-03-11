✈️ Travel RAG – Flight Q&A Backend (Spring Boot + pgvector + OpenAI)

A Retrieval-Augmented Generation (RAG) backend built with:

Spring Boot

PostgreSQL + pgvector

OpenAI Embeddings + Chat Completions

Clean layered architecture (API / Service / Repo / Domain)

This project indexes flight-related documents, stores vector embeddings in Postgres, and answers user questions using semantic retrieval + LLM generation.

🚀 Features

📄 Document indexing with automatic chunking

🧠 OpenAI embeddings (text-embedding-3-small)

🔍 Vector similarity search using pgvector

🤖 Context-aware answers using RAG

📚 Source retrieval (document name, chunk index, similarity score)

🌍 Global search or document-scoped search

🧱 Clean layered architecture

⚠️ Centralized error handling

🔒 Parameterized SQL (safe vector casting)

🏗 Architecture Overview
api/
  controller/       → REST controllers
  dto/              → API request/response objects
  ApiError          → Structured error responses
  GlobalExceptionHandler

model/              → Domain models (Document, Chunk, ChunkSource)

repo/               → JDBC + pgvector queries

service/
  RagService        → RAG orchestration
  IndexingService   → Index pipeline
  ChunkingService   → Text splitting
  PromptBuilder     → Prompt construction
  integration/openai/
      OpenAiClient  → External OpenAI integration

util/
  VectorUtils       → pgvector formatting

config/
  JacksonConfig
  OpenAiProperties
RAG Flow

Indexing

Split document into chunks

Generate embeddings

Store chunks + vectors in Postgres

Question Answering

Embed question

Retrieve top-K similar chunks via pgvector

Build prompt with retrieved context

Generate answer via OpenAI

Return answer + sources

🧠 Why RAG?

Instead of letting the LLM hallucinate, we:

Retrieve relevant chunks using vector similarity

Inject them into the prompt

Force the model to answer only from provided context

This makes answers:

More accurate

Explainable (sources included)

Deterministic based on indexed data

🗄 Database Setup
Requirements

PostgreSQL 14+

pgvector extension installed

Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;
Schema (auto-created via db/init.sql)
CREATE TABLE documents (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE chunks (
    id UUID PRIMARY KEY,
    document_id UUID REFERENCES documents(id),
    content TEXT NOT NULL,
    chunk_index INT,
    embedding vector(1536) NOT NULL
);
⚙️ Configuration

application.yml

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/travelrag
    username: postgres
    password: postgres

openai:
  apiKey: ${OPENAI_API_KEY:}

Set your OpenAI key:

export OPENAI_API_KEY=your_key_here
▶️ Running the App
mvn spring-boot:run
🧪 Example Usage
1️⃣ Index a Document
curl -X POST http://localhost:8080/api/index \
  -H "Content-Type: application/json" \
  -d '{
    "name": "barcelona-flight-guide",
    "content": "Flights from Barcelona to London cost between 70–180 EUR. Ryanair and Vueling operate this route."
  }'

Response:

{
  "status": "ok",
  "documentId": "uuid-here"
}
2️⃣ Ask a Question
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Which airlines fly from Barcelona to London?"
  }'

Response:

{
  "answer": "Ryanair and Vueling operate flights from Barcelona to London.",
  "sources": [
    {
      "documentId": "...",
      "documentName": "barcelona-flight-guide",
      "chunkIndex": 0,
      "content": "Flights from Barcelona...",
      "score": 0.12
    }
  ]
}
🛠 Problems Encountered & Solutions
❌ OutOfMemoryError during indexing

Cause: chunking loop did not ensure forward progress.
Fix: enforced overlap < chunk size and proper loop termination.

❌ bad SQL grammar

Cause: repository code diverged from DB schema.
Fix: aligned SQL with UUID-based schema and proper vector casting.

❌ ObjectMapper not autowired

Cause: Jackson auto-config not triggered.
Fix: explicit JacksonConfig bean.

❌ Vector casting concerns

Avoided string interpolation and used safe parameter binding with ?::vector.

🎯 Design Decisions
Why JDBC instead of JPA?

Direct control over pgvector queries

Simpler mental model

No ORM abstraction interfering with vector operations

Why return sources?

Improves transparency

Essential for debugging RAG

Required for production-grade UX

Why separate API DTOs from Domain models?

Keeps public contract stable

Allows domain to evolve independently

Clean separation of concerns

📈 Next Improvements

Add Testcontainers integration tests (Postgres + pgvector)

Add request validation (@NotBlank, etc.)

Add batching for embeddings

Add retry/backoff for OpenAI calls

Add OpenAPI/Swagger docs

Add React frontend
