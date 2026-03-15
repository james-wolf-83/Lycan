package com.jwolf.lycan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.jwolf.lycan.model.FileEntity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class SearchService {

    private final ChatClient chatClient; 
    private final VectorStore vectorStore;

    @PersistenceContext
    private EntityManager em;

    public SearchService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        // The builder is the standard for 2.0.0-M2
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * VICTORY TEST: This runs 5 seconds after the app starts.
     * It will search your vector store and ask Ollama to explain.
     */
    @PostConstruct
    public void startupTest() {
        new Thread(() -> {
            try {
                // Buffer to let DB and Ollama warm up
                Thread.sleep(5000); 
                
                System.out.println("\n" + "=".repeat(50));
                System.out.println("   LYCAN AUTO-RUN: ASSET INTELLIGENCE TEST");
                System.out.println("=".repeat(50));

                String myQuestion = "How do I wipe the database?";
                String answer = askLycan(myQuestion);
                
                System.out.println("\nQUESTION: " + myQuestion);
                System.out.println("LYCAN'S ANSWER: " + answer);
                System.out.println("=".repeat(50) + "\n");
                
            } catch (Exception e) {
                System.err.println("Lycan startup test failed: " + e.getMessage());
            }
        }).start();
    }

    /**
     * RAG logic: Retrieve context from VectorStore and Generate with ChatClient.
     */
    public String askLycan(String userQuery) {
        // Find the documents in PGVector
        List<String> context = semanticSearch(userQuery);
        
        return chatClient.prompt()
                .user(u -> u.text("You are Lycan. Use the following context to answer.\n\n" +
                                  "Context:\n" + context + "\n\n" +
                                  "Question: " + userQuery))
                .call()
                .content();
    }

    /**
     * Semantic Search: The core Vector Store retrieval.
     */
    public List<String> semanticSearch(String query) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(2)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);

        if (results.isEmpty()) {
            return List.of("No relevant context found in vector store.");
        }

        return results.stream()
                .map(doc -> {
                    // getText() is the 2.0.0 standard for Document content
                    String content = doc.getText(); 
                    String source = doc.getMetadata() != null ? 
                                    String.valueOf(doc.getMetadata().getOrDefault("source", "Unknown")) : "Unknown";

                    return "[Source: " + source + "] " + content;
                })
                .collect(Collectors.toList());
    }

    /**
     * Classic SQL Search: Quick filename lookup.
     */
    public void findFilesByName(String name) {
        List<FileEntity> results = em.createQuery(
                "SELECT f FROM FileEntity f WHERE f.fileName LIKE :name", FileEntity.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();

        System.out.println("\n--- FILENAME SEARCH RESULTS ---");
        if (results.isEmpty()) {
            System.out.println("No files found.");
        } else {
            results.forEach(f -> System.out.printf("[%d] %s%n", f.getId(), f.getFileName()));
        }
    }
}