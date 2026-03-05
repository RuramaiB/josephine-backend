package com.project.pricing.Images;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<ImageData, String> {
    Optional<ImageData> findByName(String filename);
}
