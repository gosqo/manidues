package com.gosqo.flyinheron.integrated;

import com.gosqo.flyinheron.domain.Token;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {"schedule.cron.token.delete-expired=0/1 * * * * *"}
)
@Slf4j
class TokenSchedulerTest extends SpringBootTestBase {
    private final TestTokenBuilder tokenBuilder;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenSchedulerTest(
            TestRestTemplate template,
            TestTokenBuilder tokenBuilder,
            MemberRepository memberRepository,
            TokenRepository tokenRepository
    ) {
        super(template);
        this.tokenBuilder = tokenBuilder;
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    void initData() {
        member = memberRepository.save(buildMember());
    }

    @Override
    @BeforeEach
    void setUp() {
        initData();
    }

    @Override
    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private Map<String, Date> buildTestTokens() {
        Map<String, Date> testTokens = new HashMap<>();
        int tokenCount = 3;
        int lastTokenIndex = tokenCount - 1;

        IntStream.range(0, tokenCount).forEach(i -> {
            long expiration = -i * 1_000_000L - 1_000_000L;

            if (i == lastTokenIndex) {
                expiration *= -1; // expiration 미래로 설정.
            }

            Date expirationDate = new Date(System.currentTimeMillis() + expiration);
            String token = tokenBuilder.buildToken(member, expiration);

            testTokens.put(token, expirationDate);
        });

        return testTokens;
    }

    private void persistMappedTokens(Map<String, Date> testTokens) {
        for (String token : testTokens.keySet()) {
            tokenRepository.save(Token.builder()
                    .member(member)
                    .token(token)
                    .expirationDate(testTokens.get(token)) // dummy Date to save expired token
                    .build());
        }
    }

    @Test
    void delete_expired_tokens() throws InterruptedException {
        persistMappedTokens(buildTestTokens());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Token> leftTokens = tokenRepository.findAll();
            assertThat(leftTokens.size()).isEqualTo(1);
        });
    }
}