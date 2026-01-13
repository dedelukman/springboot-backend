package com.abahstudio.app.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {


    private final FileService fileService;
    private final StorageService storageService;


    @PostMapping
    public FileEntity upload(
            @RequestParam MultipartFile file,
            @RequestParam String ownerType,
            @RequestParam String ownerId
    ) {
        return fileService.upload(file, ownerType, ownerId);
    }


    @GetMapping("/owner")
    public List<FileEntity> byOwner(
            @RequestParam String ownerType,
            @RequestParam String ownerId
    ) {
        return fileService.findByOwner(ownerType, ownerId);
    }


    @GetMapping("/{storageKey}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String storageKey) {
        var stream = storageService.load(storageKey);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/view/{storageKey}")
    public ResponseEntity<InputStreamResource> view(@PathVariable String storageKey) {

        FileEntity file = fileService.findByStorageKey(storageKey);

        InputStreamResource resource =
                new InputStreamResource(storageService.load(storageKey));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

}
