package com.abahstudio.app.domain.notification;

import com.abahstudio.app.domain.notification.dto.NotificationResponse;
import com.abahstudio.app.domain.notification.dto.NotificationUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public List<NotificationResponse> inbox() {
        return service.inbox();
    }

    @GetMapping("/favorites")
    public List<NotificationResponse> favorites() {
        return service.favorites();
    }

    @GetMapping("/archived")
    public List<NotificationResponse> archived() {
        return service.archived();
    }

    @PatchMapping("/{id}")
    public NotificationResponse update(
            @PathVariable Long id,
            @RequestBody NotificationUpdateRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
