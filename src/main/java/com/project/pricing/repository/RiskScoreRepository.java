package com.project.pricing.repository;

import com.project.pricing.model.RiskScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RiskScoreRepository extends MongoRepository<RiskScore, String> {
    Optional<RiskScore> findFirstByProductIdOrderByTimestampDesc(String productId);
}
