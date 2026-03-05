package com.project.pricing.repository;

import com.project.pricing.model.PriceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceRecordRepository extends MongoRepository<PriceRecord, String> {
    List<PriceRecord> findByProductId(String productId);

    List<PriceRecord> findBySource(String source);

    List<PriceRecord> findByRegion(String region);

    List<PriceRecord> findBySourceAndRegion(String source, String region);
}
