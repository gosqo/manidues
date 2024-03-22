package com.vong.manidues.common;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String getHome() {
        log.info("request to \"/\" ... ");
        return "index";
    }

    @GetMapping("/board/{id}")
    public String getBoard(@NotNull @PathVariable("id") Long id) {
        log.info("request to board id: {}", id);
        return "board/board";
    }

    @GetMapping("/board/{id}/modify")
    public String modifyBoard(@NotNull @PathVariable("id") Long id) {
        log.info("request to board id: {}", id);
        return "board/boardModify";
    }

    @GetMapping(value = {"/boards", "/boards/{pageNumber}"})
    public String getBoardListView(@Nullable @PathVariable("pageNumber") Integer pageNumber) {
        return "board/boardList";
    }

    @GetMapping("/board/new")
    public String newBoard() { return "board/newBoard"; }
}
