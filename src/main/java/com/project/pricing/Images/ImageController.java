package com.project.pricing.Images;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageServices imageServices;

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageServices.uploadImage(file));
    }
    @GetMapping("/get-image/{filename}")
    public byte[] downloadImage(@PathVariable String filename) throws IOException {
        return imageServices.downloadImage(filename);
    }
}
