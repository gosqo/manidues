package com.vong.manidues.integrated;

import com.vong.manidues.domain.Token;
import com.vong.manidues.repository.BoardRepository;
import com.vong.manidues.repository.CommentRepository;
import com.vong.manidues.repository.MemberRepository;
import com.vong.manidues.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {"schedule.cron.token.delete-expired=0/1 * * * * *"}
)
@Slf4j
class TokenSchedulerTest extends SpringBootTestBase {
    private final TestTokenBuilder tokenBuilder;

    @Autowired
    public TokenSchedulerTest(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            BoardRepository boardRepository,
            CommentRepository commentRepository,
            TestRestTemplate template,
            TestTokenBuilder tokenBuilder
    ) {
        super(memberRepository, boardRepository, commentRepository, tokenRepository, template);
        this.tokenBuilder = tokenBuilder;
    }

    @BeforeEach
    void setUp() {
        initMember();
    }

    private void saveTokens() {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            long expiration = -i * 1_000_000L - 1_000_000L;

            if (i == 2) expiration = -expiration;
            String token = tokenBuilder.buildToken(new HashMap<>(), member, expiration);
            tokens.add(token);
        }

        for (String token : tokens) {
            tokenRepository.save(Token.builder().member(member).token(token).expirationDate(new Date()) // dummy
                    .build());
        }
    }

    private void logStoredTokens() {
        tokenBuilder.getStoredTokens().forEach(i -> log.info(i.toString()));
    }

    @Test
    void delete_expired_tokens() throws InterruptedException {
        saveTokens();
        logStoredTokens();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Token> leftTokens = tokenRepository.findAll();
            assertThat(leftTokens.size()).isEqualTo(1);
        });

        logStoredTokens();
    }
}