package com.danifb.travel_rag.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ChunkRepository {
    private final JdbcTemplate jdbc;

    public ChunkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID id, UUID documentId, String content, String embeddingLiteral) {
        jdbc.update("""
                INSERT INTO chunks (id, document_id, content, embedding)
                VALUES (?, ?, ?, ?::vector)
                """,
                id, documentId, content, embeddingLiteral
        );
    }
}