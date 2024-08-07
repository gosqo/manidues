package com.vong.manidues.domain.comment;

import com.vong.manidues.domain.comment.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService service;

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<CommentDeleteResponse> removeComment(
            @PathVariable("id") Long id
            , HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.remove(id, request));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentUpdateResponse> modifyComment(
            @PathVariable("id") Long id
            , HttpServletRequest request
            , @Valid @RequestBody CommentUpdateRequest requestBody
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.modify(id, request, requestBody));
    }

    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity<CommentPageResponse> getPageOfComment(
            @PathVariable("boardId") Long boardId
            , @RequestParam("page-number") int pageNumber
    ) throws NoResourceFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getCommentSliceOf(boardId, pageNumber));
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentGetResponse> getComment(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.get(id));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/comment")
    public ResponseEntity<CommentRegisterResponse> registerComment(
            HttpServletRequest request
            , @Valid @RequestBody CommentRegisterRequest requestBody
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.register(request, requestBody));
    }
}
