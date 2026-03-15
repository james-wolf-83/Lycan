package com.jwolf.lycan.service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.jwolf.lycan.model.FileEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FileIndexer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private IngestionService ingestionService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final AtomicInteger fileCount = new AtomicInteger(0);

    @Override
    public void run(String... args) throws Exception {
        String pathToScan = (args.length > 0) ? args[0] : ".";
        System.out.println("=== LYCAN STARTING SCAN: " + pathToScan + " ===");
        fileScanner(pathToScan);
    }

    public void fileScanner(String folderToScan) throws IOException {
        long startTime = System.currentTimeMillis();
        fileCount.set(0);

        // Using executeWithoutResult fixes the "Cannot infer type argument" error
        transactionTemplate.executeWithoutResult(status -> {
            try {
                Files.walkFileTree(Paths.get(folderToScan), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String absolutePath = file.toAbsolutePath().normalize().toString();

                        Long count = em.createQuery("SELECT count(f) FROM FileEntity f WHERE f.filePath = :path", Long.class)
                                .setParameter("path", absolutePath)
                                .getSingleResult();

                        if (count == 0) {
                            FileEntity entity = new FileEntity(
                                file.getFileName().toString(),
                                absolutePath,
                                attrs.size());
                        
                            // Save to Registry
                            em.persist(entity);

                            // Immediate handover to AI
                            try {
                                ingestionService.embedSingleFile(entity.getFilePath());
                                // Ensure your FileEntity has this method and the DB has the column
                                entity.setEmbedded(true); 
                            } catch (Exception e) {
                                System.err.println("AI failed to read: " + absolutePath);
                            }
                            
                            fileCount.incrementAndGet();
                        }
                        
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        String name = dir.getFileName().toString();
                        if (name.equals("node_modules") || name.equals(".git") || name.equals("target")) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Scan Complete: " + fileCount.get() + " files indexed in " + duration + "ms");
    }
}