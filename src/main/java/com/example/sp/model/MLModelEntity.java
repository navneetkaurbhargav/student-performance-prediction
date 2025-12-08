package com.example.sp.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ml_models")
public class MLModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Lob
    @Column(name = "model_blob", nullable=false)
    private byte[] modelBlob;

    @Column(name="created_at")
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public byte[] getModelBlob() { return modelBlob; }
    public void setModelBlob(byte[] modelBlob) { this.modelBlob = modelBlob; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
