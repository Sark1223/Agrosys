package com.agrosys.worker.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final Cloudinary cloudinary;

    private String extractBase64(String data) {
        if (data == null) return null;
        if (data.contains(",")) {
            return data.substring(data.indexOf(",") + 1);
        }
        return data;
    }

    public String uploadImage(String base64Data) {
        try {
            String pureBase64 = extractBase64(base64Data);
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);
            return uploadBytes(imageBytes, "worker_photo");
        } catch (IllegalArgumentException e) {
            log.error("[INVALID BASE64] - {}", e.getMessage());
            throw new RuntimeException("Formato Base64 inválido");
        } catch (IOException e) {
            log.error("[UPLOAD FAILED] - {}", e.getMessage());
            throw new RuntimeException("Error al subir imagen a Cloudinary");
        }
    }

    public String uploadImage(String base64Data, String publicId) {
        try {
            String pureBase64 = extractBase64(base64Data);
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);
            return uploadBytes(imageBytes, publicId);
        } catch (IllegalArgumentException e) {
            log.error("[INVALID BASE64] - {}", e.getMessage());
            throw new RuntimeException("Formato Base64 inválido");
        } catch (IOException e) {
            log.error("[UPLOAD FAILED] - {}", e.getMessage());
            throw new RuntimeException("Error al subir imagen a Cloudinary");
        }
    }

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            return uploadBytes(file.getBytes(), file.getOriginalFilename());
        } catch (IOException e) {
            log.error("[UPLOAD FAILED] - {}", e.getMessage());
            throw new RuntimeException("Error al procesar imagen");
        }
    }

    public String uploadBytes(byte[] imageBytes, String publicId) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(imageBytes,
                ObjectUtils.asMap("public_id", publicId, "folder", "agrosys/workers"));
        String url = (String) result.get("secure_url");
        log.info("[UPLOAD SUCCESS] - URL: {}", url);
        return url;
    }

    public void deleteImage(String publicId) {
        try {
            String fullPublicId = "agrosys/workers/" + publicId;
            Map<String, Object> result = cloudinary.uploader().destroy(fullPublicId, ObjectUtils.emptyMap());
            log.info("[DELETE SUCCESS] - Deleted: {}, result: {}", fullPublicId, result);
        } catch (IOException e) {
            log.error("[DELETE FAILED] - {}", e.getMessage());
            throw new RuntimeException("Error al eliminar imagen de Cloudinary: " + e.getMessage());
        }
    }
}