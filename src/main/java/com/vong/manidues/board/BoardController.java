package com.vong.manidues.board;

import com.vong.manidues.board.dto.*;
import com.vong.manidues.utility.ServletRequestUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService service;
    private final ServletRequestUtility servletRequestUtility;

    @GetMapping("/")
    public ResponseEntity<BoardListWithPageGetResponse> getBoardList(
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(new BoardListWithPageGetResponse().fromEntityList(service.getBoardPage(pageable).getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGetResponse> getBoard(
            @PathVariable("id") Long id,
            HttpServletRequest servletRequest
    ) {
        String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.info("GET /api/v1/board/{} with token: \n{}", id, servletRequestUtility.extractJwtFromHeader(servletRequest));
        }

        log.info("GET /api/v1/board/{} without token.", id);
        Board entity = service.get(id);

        return entity != null
                ? ResponseEntity.ok(
                new BoardGetResponse().fromEntity(entity)
        )
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BoardDeleteResponse> deleteBoard(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long id
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        return service.delete(id, requestUserEmail)
                ? ResponseEntity.ok(
                BoardDeleteResponse.builder()
                        .isDeleted(true)
                        .message("삭제되었습니다.")
                        .build()
        )
                : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardUpdateResponse> updateBoard(
            HttpServletRequest servletRequest,
            @PathVariable("id") Long id,
            @RequestBody BoardUpdateRequest request
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        return service.update(id, requestUserEmail, request)
                ? ResponseEntity.ok(
                BoardUpdateResponse.builder()
                        .id(id)
                        .isUpdated(true)
                        .message("해당 게시물의 수정이 처리됐습니다.")
                        .build()
        )
                : ResponseEntity.badRequest().build();
    }

    @PostMapping("")
    public ResponseEntity<BoardRegisterResponse> registerBoard(
            HttpServletRequest servletRequest,
            @Valid @RequestBody BoardRegisterRequest request
    ) {
        String requestUserEmail = servletRequestUtility
                .extractEmailFromHeader(servletRequest);

        log.info("request to POST /api/v1/board/ Email is : {}", requestUserEmail);

        Long id = service.register(requestUserEmail, request);

        return id != null
                ? ResponseEntity.ok(
                BoardRegisterResponse.builder()
                        .id(id)
                        .posted(true)
                        .message("게시물 등록이 완료됐습니다.")
                        .build()
        )
                : ResponseEntity.badRequest().build();
    }


}
