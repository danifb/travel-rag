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

        System.out.println("Searching similar chunks...");
        System.out.println("Vector length: " + queryEmbeddingLiteral.length());

        // ⬅️ Interpolamos el vector directamente en el SQL
        // en lugar de pasarlo como parámetro ?
        // porque el driver JDBC no maneja bien el cast a vector
        String sql = String.format("""
            SELECT content
            FROM chunks
            ORDER BY embedding <=> '%s'::vector
            LIMIT %d
            """, queryEmbeddingLiteral, topK);

        List<String> results = jdbc.query(
                sql,
                (rs, rowNum) -> rs.getString("content")
        );

        System.out.println("Results found: " + results.size());
        return results;
    }
}