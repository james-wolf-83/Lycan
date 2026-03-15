package com.jwolf.lycan.service;

import com.jwolf.lycan.model.FileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Tells Spring to manage this class
public class SearchService {

    @PersistenceContext // Spring injects the EntityManager automatically
    private EntityManager em;

    public void findFilesByName(String name) {
        List<FileEntity> results = em.createQuery(
            "SELECT f FROM FileEntity f WHERE f.fileName LIKE :name", FileEntity.class)
            .setParameter("name", "%" + name + "%")
            .getResultList();

        // ... keep your print logic here ...
        System.out.println("\n--- SEARCH RESULTS For: " + name + "---");
            if (results.isEmpty()) {
                System.out.println("No files found.");
            }else {
                for (FileEntity file : results) {
                    System.out.printf("[%d] %s | %d bytes | %s%n",
                        file.getId(), file.getFileName(), file.getFileSize(), file.getFilePath());
                }
            }
            System.out.println("-----------------------------------------");
    }
}