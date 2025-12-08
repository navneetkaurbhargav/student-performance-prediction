package com.example.sp.repository;

import com.example.sp.model.MLModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MLModelRepository extends JpaRepository<MLModelEntity, Long> {
    Optional<MLModelEntity> findTopByOrderByCreatedAtDesc();
}
