package com.vong.manidues.board.dto;

import com.vong.manidues.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageResponse {

    private Page<BoardGetResponse> boardPage;

    public static BoardPageResponse fromEntityPage(Page<Board> entityPage) {
        return BoardPageResponse.builder()
                .boardPage(new PageImpl<>(
                        entityPage.get().map(BoardGetResponse::of).toList(),
                        entityPage.getPageable(),
                        entityPage.getTotalElements()
                ))
                .build();
    }
}
