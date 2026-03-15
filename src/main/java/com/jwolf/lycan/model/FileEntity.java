package com.jwolf.lycan.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "indexed_files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    
    private Long fileSize;
    private LocalDateTime indexedAt;

    @Column(unique = true, nullable = false)
    private String filePath;

    @Column(nullable= false)
    private boolean isEmbedded= false;

    public FileEntity() {
        this.indexedAt = LocalDateTime.now();
    }

    public FileEntity(String fileName, String filePath, Long fileSize) {
        this();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // Getters
    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public Long getFileSize() { return fileSize; }
    public LocalDateTime getDateTime() { return indexedAt;}
    public boolean isEmbedded() { return isEmbedded;}
    // Setters
    public void setEmbedded(boolean embedded) { isEmbedded = embedded;}
}