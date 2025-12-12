package com.studentperformance.repository;

import com.studentperformance.model.domain.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PredictionHistoryRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByStudentId(Long studentId);
    List<Prediction> findByModelUsed(String modelName);
}