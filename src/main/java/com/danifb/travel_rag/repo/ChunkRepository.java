package com.danifb.travel_rag.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ChunkRepository {

    private final JdbcTemplate jdbc;

    public ChunkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID id, UUID documentId, String content,
                       int chunkIndex, String embeddingLiteral) {
        jdbc.update("""
                INSERT INTO chunks (id, document_id, content, chunk_index, embedding)
                VALUES (?, ?, ?, ?, ?::vector)
                """,
                id, documentId, content, chunkIndex, embeddingLiteral
        );
    }

    public List<String> findSimilarChunks(String queryEmbeddingLiteral, int topK) {
        String sql = """
                SELECT content
                FROM chunks
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """;

        return jdbc.query(
                sql,
                (rs, rowNum) -> rs.getString("content"),
                queryEmbeddingLiteral,
                topK
        );
    }
}