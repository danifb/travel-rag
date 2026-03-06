package com.danifb.travel_rag.service;

import com.danifb.travel_rag.model.Chunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChunkingService {

    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;

    public List<Chunk> chunkDocument(UUID documentId, String content) {
        List<Chunk> chunks = new ArrayList<>();

        // Normalizar saltos de línea primero
        // Convertir \n\n o \r\n\r\n en separador estándar
        String normalized = content
                .replace("\\n", "\n")      // por si viene escapado del JSON
                .replace("\r\n", "\n")     // Windows line endings
                .trim();

        // Dividir por línea doble (párrafos)
        String[] paragraphs = normalized.split("\n\n+");

        System.out.println("Párrafos encontrados: " + paragraphs.length);

        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;

            // Si añadir este párrafo supera el límite
            if (currentChunk.length() + paragraph.length() > CHUNK_SIZE
                    && currentChunk.length() > 0) {

                // Guardar chunk actual
                String chunkContent = currentChunk.toString().trim();
                System.out.println("Chunk " + chunkIndex + " creado: "
                        + chunkContent.length() + " chars");

                chunks.add(new Chunk(
                        UUID.randomUUID(),
                        documentId,
                        chunkContent,
                        chunkIndex++
                ));

                // Empezar nuevo chunk con solapamiento
                String previousText = currentChunk.toString();
                int overlapStart = Math.max(0, previousText.length() - OVERLAP);
                currentChunk = new StringBuilder(previousText.substring(overlapStart));
            }

            currentChunk.append(paragraph).append("\n\n");
        }

        // Guardar el último chunk
        if (!currentChunk.isEmpty()) {
            String chunkContent = currentChunk.toString().trim();
            System.out.println("Chunk " + chunkIndex + " creado: "
                    + chunkContent.length() + " chars");

            chunks.add(new Chunk(
                    UUID.randomUUID(),
                    documentId,
                    chunkContent,
                    chunkIndex
            ));
        }

        System.out.println("Total chunks: " + chunks.size());
        return chunks;
    }
}