package com.acpulse.controller;

import com.acpulse.dto.response.GlobalSearchResponse;
import com.acpulse.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<GlobalSearchResponse>> search(@RequestParam(value = "q", defaultValue = "") String query) {
        return ResponseEntity.ok(searchService.globalSearch(query));
    }
}
