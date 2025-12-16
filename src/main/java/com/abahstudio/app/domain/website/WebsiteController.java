package com.abahstudio.app.domain.website;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/website")
@RequiredArgsConstructor
public class WebsiteController {

    private final WebsiteService websiteService;

    @GetMapping
    public Website getWebsite() {
        return websiteService.get();
    }

    @PutMapping
    public Website saveWebsite(@RequestBody Website website) {
        return websiteService.save(website);
    }
}

