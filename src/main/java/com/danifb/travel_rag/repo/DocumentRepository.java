package com.danifb.travel_rag.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class DocumentRepository {

    private final JdbcTemplate jdbc;

    public DocumentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID id, String name) {
        jdbc.update(
                "INSERT INTO documents (id, name) VALUES (?, ?)",
                id, name
        );
    }
}