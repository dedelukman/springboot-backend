package com.abahstudio.app.domain.subscription;

import com.abahstudio.app.domain.subscription.dto.SubscriptionResponse;
import com.abahstudio.app.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @GetMapping
    public SubscriptionResponse current() {
        return service.getCurrent();
    }

//    @PostMapping("/subscribe/{planCode}")
//    public void subscribe(@PathVariable String planCode) {
//        service.subscribe(planCode);
//    }
}

