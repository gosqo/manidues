package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.board.BoardPageResponse;
import com.gosqo.flyinheron.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardPageController {

    private final BoardService service;

    @GetMapping("/{pageNumber}")
    public ResponseEntity<BoardPageResponse> getBoardList(
            @PathVariable("pageNumber") int pageNumber
    ) throws NoResourceFoundException {
        return ResponseEntity.status(200).body(service.getBoardPage(pageNumber));
    }
}
