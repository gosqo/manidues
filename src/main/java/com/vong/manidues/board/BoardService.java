package com.vong.manidues.board;

import com.vong.manidues.board.dto.BoardRegisterRequest;
import com.vong.manidues.board.dto.BoardUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BoardService {

    Page<Board> getBoardPage(Pageable pageable);

    Long register(String userEmail, BoardRegisterRequest request);

    boolean update(Long id,
                   String requestUserEmail,
                   BoardUpdateRequest request);

    boolean delete(Long id, String requestUserEmail);

    Board get(Long id);
}
