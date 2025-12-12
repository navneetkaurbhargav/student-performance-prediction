package com.studentperformance.repository;

import com.studentperformance.model.domain.MLModel;
import com.studentperformance.model.dto.ModelStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<MLModel, Long> {
    List<MLModel> findByIsActive(Boolean isActive);
    MLModel findByModelName(String modelName);
    List<MLModel> findByAlgorithm(String algorithm);

}