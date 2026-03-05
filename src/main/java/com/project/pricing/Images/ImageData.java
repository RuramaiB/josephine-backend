package com.project.pricing.Images;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
@Builder
public class ImageData {
    @Id
    private String imageID;
    private String name;
    private String type;
    private String filePath;
}
