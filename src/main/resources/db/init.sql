CREATE
EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS documents
(
    id UUID PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS chunks
(
    id UUID PRIMARY KEY,
    document_id UUID REFERENCES documents(id),
    content TEXT NOT NULL,
    embedding vector(1536) NOT NULL
);