<h1>✈️ Travel RAG – Flight Q&amp;A Backend</h1>

<p>
A Retrieval-Augmented Generation (RAG) backend built with:
</p>

<ul>
  <li>Spring Boot</li>
  <li>PostgreSQL + pgvector</li>
  <li>OpenAI Embeddings + Chat API</li>
  <li>Clean layered architecture</li>
</ul>

<p>
This service indexes flight-related documents, stores vector embeddings in Postgres,
and answers questions using semantic retrieval + LLM generation.
</p>

<div class="section">
<h2>What It Does</h2>

<h3>Index Documents</h3>
<ul>
  <li>Split document into chunks</li>
  <li>Generate embeddings via OpenAI</li>
  <li>Store chunks + vectors in Postgres</li>
</ul>

<h3>Answer Questions</h3>
<ul>
  <li>Embed user question</li>
  <li>Retrieve top-K similar chunks via pgvector</li>
  <li>Build contextual prompt</li>
  <li>Generate answer via OpenAI</li>
  <li>Return answer with sources</li>
</ul>
</div>

<div class="section">
<h2>Architecture</h2>

<pre><code>
api/
  controller/     → REST endpoints
  dto/            → API request/response objects
  ApiError        → Structured error responses
  GlobalExceptionHandler

model/            → Domain models (Document, Chunk, ChunkSource)

repo/             → JDBC + pgvector queries

service/
  RagService
  IndexingService
  ChunkingService
  PromptBuilder
  integration/openai/
    OpenAiClient

util/
  VectorUtils

config/
  JacksonConfig
  OpenAiProperties
</code></pre>

<p>
Design notes:
</p>
<ul>
  <li>Parameterized SQL with safe vector casting (<code>?::vector</code>)</li>
  <li>JDBC used intentionally for direct pgvector control</li>
  <li>Sources returned for explainability</li>
</ul>
</div>

<div class="section">
<h2>Database Setup</h2>

<pre><code>
CREATE EXTENSION IF NOT EXISTS vector;

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
</code></pre>
</div>

<div class="section">
<h2>Configuration</h2>

<pre><code>
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/travelrag
    username: postgres
    password: postgres

openai:
  apiKey: ${OPENAI_API_KEY:}
</code></pre>

<p>Set your OpenAI key:</p>

<pre><code>
export OPENAI_API_KEY=your_key_here
</code></pre>
</div>

<div class="section">
<h2>Run</h2>

<pre><code>
mvn spring-boot:run
</code></pre>
</div>

<div class="section">
<h2>Example Usage</h2>

<h3>Index a Document</h3>

<pre><code>
curl -X POST http://localhost:8080/api/index \
  -H "Content-Type: application/json" \
  -d '{
    "name": "barcelona",
    "content": "Flights from Barcelona to London cost 70–180 EUR. Ryanair and Vueling operate this route."
  }'
</code></pre>

<h3>Ask a Question</h3>

<pre><code>
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Which airlines fly from Barcelona to London?"
  }'
</code></pre>

<h3>Example Response</h3>

<pre><code>
{
  "answer": "Ryanair and Vueling operate flights from Barcelona to London.",
  "sources": [
    {
      "documentId": "...",
      "documentName": "barcelona",
      "chunkIndex": 0,
      "content": "Flights from Barcelona to London cost 70–180 EUR...",
      "score": 0.12
    }
  ]
}
</code></pre>
</div>

<div class="section">
<h2>Challenges Solved</h2>
<ul>
  <li>Fixed chunking edge-case causing OutOfMemoryError</li>
  <li>Resolved SQL errors from schema mismatches</li>
  <li>Implemented safe pgvector casting</li>
  <li>Added global + document-scoped retrieval</li>
</ul>
</div>

<div class="section">
<h2>Why This Project Matters</h2>
<ul>
  <li>Demonstrates real-world LLM backend integration</li>
  <li>Uses proper vector search instead of naive prompting</li>
  <li>Clean architecture ready for testing and frontend integration</li>
  <li>Explainable AI approach</li>
</ul>
</div>
