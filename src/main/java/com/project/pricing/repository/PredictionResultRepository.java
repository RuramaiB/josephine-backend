package com.project.pricing.repository;

import com.project.pricing.model.PredictionResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PredictionResultRepository extends MongoRepository<PredictionResult, String> {
    Optional<PredictionResult> findFirstByProductIdOrderByTimestampDesc(String productId);
}
