package com.abahstudio.app.core.file;

import com.abahstudio.app.core.exception.ApiException;
import com.abahstudio.app.core.exception.ErrorCode;
import com.abahstudio.app.core.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileService {

    private final Path uploadDir;

    // Konstanta lainnya tetap hardcoded
    private static final List<String> ALLOWED_EXT = List.of("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_MIME = List.of("image/jpeg", "image/png");
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2 MB

    private static final Map<String, byte[]> MAGIC_NUMBERS = Map.of(
            "jpeg", new byte[]{ (byte)0xFF, (byte)0xD8 },
            "png",  new byte[]{ (byte)0x89, 0x50, 0x4E, 0x47 }
    );

    // Constructor dengan @Value hanya untuk upload directory
    public FileService(
            @Value("${app.file.upload-dir:uploads/}") String uploadDirPath) {

        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadDir);
            System.out.println("Upload directory initialized: " + this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDirPath, e);
        }
    }

    public String upload(MultipartFile file, String existingFile) {
        if (file == null || file.isEmpty()) return existingFile;

        // --- SIZE VALIDATION ---
        if (file.getSize() > MAX_SIZE) {
            throw new FileValidationException(ErrorCode.FILE_TOO_LARGE);
        }

        // --- EXT VALIDATION ---
        String ext = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!ALLOWED_EXT.contains(ext)) {
            throw new FileValidationException(ErrorCode.FILE_INVALID_EXTENSION);
        }

        // --- MIME VALIDATION ---
        if (!ALLOWED_MIME.contains(file.getContentType())) {
            throw new FileValidationException(ErrorCode.FILE_INVALID_MIME);
        }

        // --- MAGIC NUMBER VALIDATION ---
        try {
            byte[] bytes = file.getInputStream().readNBytes(4);
            byte[] expected = MAGIC_NUMBERS.get(ext.equals("jpg") ? "jpeg" : ext);

            for (int i = 0; i < expected.length; i++) {
                if (bytes[i] != expected[i]) {
                    throw new FileValidationException(ErrorCode.FILE_INVALID_MAGIC_NUMBER);
                }
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }

        // --- DELETE OLD FILE IF EXISTS ---
        if (existingFile != null) {
            try {
                Path existingPath = uploadDir.resolve(existingFile);
                Files.deleteIfExists(existingPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete existing file: " + existingFile, e);
            }
        }

        // --- SAVE NEW FILE ---
        String newName = System.currentTimeMillis() + "." + ext;
        Path target = uploadDir.resolve(newName);

        try {
            Files.copy(file.getInputStream(), target);
            return newName;
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR, "Unable to save file");
        }
    }

    private String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new FileValidationException(ErrorCode.FILE_INVALID_EXTENSION);
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // Helper method untuk mendapatkan full path (jika diperlukan)
    public Path getFullPath(String filename) {
        return uploadDir.resolve(filename);
    }

    // Getter untuk upload directory (jika diperlukan di tempat lain)
    public Path getUploadDir() {
        return uploadDir;
    }
}