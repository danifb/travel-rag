package com.danifb.travel_rag.repo;

import com.danifb.travel_rag.model.Chunk;
import com.danifb.travel_rag.model.ChunkSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

@Repository
public class ChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void ensureDocument(UUID documentId, String name) {
        String sql = """
                INSERT INTO documents (id, name)
                VALUES (?, ?)
                ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
                """;
        jdbcTemplate.update(sql, documentId, name);
    }

    public void saveChunk(Chunk chunk, String embeddingLiteral) {
        saveChunk(
                chunk.getId(),
                chunk.getDocumentId(),
                chunk.getContent(),
                chunk.getChunkIndex(),
                embeddingLiteral
        );
    }

    public void saveChunk(UUID chunkId,
                          UUID documentId,
                          String content,
                          int chunkIndex,
                          String embeddingLiteral) {
        String sql = """
                INSERT INTO chunks (id, document_id, content, chunk_index, embedding)
                VALUES (?, ?, ?, ?, ?::vector)
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, chunkId);
            ps.setObject(2, documentId);
            ps.setString(3, content);
            ps.setInt(4, chunkIndex);
            ps.setString(5, embeddingLiteral);
            return ps;
        });
    }

    public List<String> findSimilarChunksGlobal(String queryEmbeddingLiteral, int topK) {
        String sql = """
            SELECT content
            FROM chunks
            ORDER BY embedding <=> (?::vector)
            LIMIT ?
            """;

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, queryEmbeddingLiteral);
            ps.setInt(2, topK);
            return ps;
        }, (rs, rowNum) -> rs.getString("content"));
    }

    public List<ChunkSource> findSimilarSourcesForDocuments(List<UUID> documentIds,
                                                            String queryEmbeddingLiteral,
                                                            int topK) {
        boolean filter = documentIds != null && !documentIds.isEmpty();

        String baseSql = """
        SELECT d.id AS document_id,
               d.name AS document_name,
               c.chunk_index,
               c.content,
               (c.embedding <=> (?::vector)) AS score
        FROM chunks c
        JOIN documents d ON d.id = c.document_id
        """;

        String where = "";
        if (filter) {
            String placeholders = String.join(",", documentIds.stream().map(id -> "?").toList());
            where = " WHERE c.document_id IN (" + placeholders + ") ";
        }

        String tailSql = """
        ORDER BY c.embedding <=> (?::vector)
        LIMIT ?
        """;

        String sql = baseSql + where + tailSql;

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            int idx = 1;

            // First vector parameter (for score select)
            ps.setString(idx++, queryEmbeddingLiteral);

            // Optional doc filters
            if (filter) {
                for (UUID id : documentIds) {
                    ps.setObject(idx++, id);
                }
            }

            // Second vector parameter (for ORDER BY)
            ps.setString(idx++, queryEmbeddingLiteral);

            // limit
            ps.setInt(idx, topK);

            return ps;
        }, (rs, rowNum) -> new ChunkSource(
                (UUID) rs.getObject("document_id"),
                rs.getString("document_name"),
                rs.getInt("chunk_index"),
                rs.getString("content"),
                rs.getDouble("score")
        ));
    }
}