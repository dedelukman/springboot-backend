package com.abahstudio.app.core.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @TempDir
    Path tempDir;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        // Menggunakan constructor dengan path dari temp directory untuk testing
        fileService = new FileService(tempDir.toString() + "/");
    }

    @Test
    void shouldCreateUploadDirectoryOnInitialization() throws IOException {
        // Arrange & Act
        // Directory sudah dibuat di constructor

        // Assert
        assertThat(Files.exists(tempDir)).isTrue();
        assertThat(Files.isDirectory(tempDir)).isTrue();
    }

    @Test
    void upload_shouldSaveFileToConfiguredDirectory() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L); // 1KB
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");

        // JPEG magic number
        byte[] jpegBytes = new byte[]{(byte)0xFF, (byte)0xD8, 0x00, 0x00};
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(jpegBytes));

        // Act
        String savedFilename = fileService.upload(file, null);

        // Assert
        Path expectedPath = tempDir.resolve(savedFilename);
        assertThat(Files.exists(expectedPath)).isTrue();
        assertThat(savedFilename).endsWith(".jpg");
    }

    @Test
    void upload_shouldDeleteExistingFile() throws IOException {
        // Arrange
        String existingFilename = "old-file.jpg";
        Path existingFilePath = tempDir.resolve(existingFilename);
        Files.write(existingFilePath, "old content".getBytes());

        MultipartFile newFile = mock(MultipartFile.class);
        when(newFile.isEmpty()).thenReturn(false);
        when(newFile.getSize()).thenReturn(1024L);
        when(newFile.getOriginalFilename()).thenReturn("new.jpg");
        when(newFile.getContentType()).thenReturn("image/jpeg");

        // JPEG magic number
        byte[] jpegBytes = new byte[]{(byte)0xFF, (byte)0xD8, 0x00, 0x00};
        when(newFile.getInputStream()).thenReturn(new ByteArrayInputStream(jpegBytes));

        // Act
        fileService.upload(newFile, existingFilename);

        // Assert
        assertThat(Files.exists(existingFilePath)).isFalse();
    }

    @Test
    void getFullPath_shouldResolveRelativeFilename() {
        // Arrange
        String filename = "test.jpg";

        // Act
        Path fullPath = fileService.getFullPath(filename);

        // Assert
        assertThat(fullPath).isEqualTo(tempDir.resolve(filename));
    }
}