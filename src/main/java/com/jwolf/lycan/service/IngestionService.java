package com.jwolf.lycan.service;

import java.util.List;

import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    private final EntityManager em;

    public IngestionService(VectorStore vectorStore, EntityManager em) {
        this.vectorStore = vectorStore;
        this.em = em;
    }

    // THIS IS THE METHOD FILEINDEXER IS SCREAMING FOR
    public void embedSingleFile(String path) {
        Resource doc = new FileSystemResource(path);
        if (doc.exists()) {
            TextReader reader = new TextReader(doc);
            var chunks = new TokenTextSplitter().apply(reader.get());
            vectorStore.accept(chunks);
            System.out.println("AI Ingested: " + doc.getFilename());
        }
    }

    // Keeps your existing "bulk" logic just in case
    public void ingestFromRegistry() {
        List<String> paths = em.createQuery("SELECT f.filePath FROM FileEntity f", String.class)
                               .getResultList();
        for (String path : paths) {
            embedSingleFile(path);
        }
    }
}