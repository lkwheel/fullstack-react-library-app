package com.wheelerkode.library.controllers;

import com.wheelerkode.library.entity.History;
import com.wheelerkode.library.services.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/histories")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/protected/findhistorybyuseremail")
    public Page<History> getHistoryByUserEmail(@RequestParam("userEmail") String userEmail, Pageable pageable) {
        return historyService.getHistoryByUserEmail(userEmail, pageable);
    }
}
