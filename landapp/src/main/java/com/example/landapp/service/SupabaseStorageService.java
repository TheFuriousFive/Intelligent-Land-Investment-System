package com.example.landapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException, InterruptedException {
        // Generate a unique file name
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFilename;

        // Construct the Supabase API endpoint for uploading
        String uploadEndpoint = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + uniqueFileName;

        // Build the HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadEndpoint))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .header("Content-Type", file.getContentType()) // e.g., image/jpeg or application/pdf
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new RuntimeException("Failed to upload file to Supabase: " + response.body());
        }

        // Return the public URL so you can save it to your database
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + uniqueFileName;
    }
}