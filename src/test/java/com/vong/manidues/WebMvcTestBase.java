package com.vong.manidues;

import com.vong.manidues.domain.member.MemberRepository;
import com.vong.manidues.domain.token.ClaimExtractor;
import com.vong.manidues.global.config.ApplicationConfig;
import com.vong.manidues.global.config.SecurityConfig;
import com.vong.manidues.global.utility.AuthHeaderUtility;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
// 앱 컨텍스트에서 가져와 사용할 설정 클래스
// 해당 클래스가 가진 의존성을 스프링을 통해 해결.
// 실제 앱 컨텍스트와 같이 작동`.
@Import(
        value = {
                SecurityConfig.class
                , ApplicationConfig.class
        }
)
// @Import 에 등록한 객체가 의존하는 객체의 의존성 해결을 위한 MockBeans
// 주입받은 의존성 객체가 의존하는 객체를 목으로 처리.
// 동작하는 척 아무일도 하지 않도록 설정하는 클래스들.
@MockBeans(
        {
                @MockBean(ClaimExtractor.class)
                , @MockBean(AuthHeaderUtility.class)
                , @MockBean(MemberRepository.class)
                , @MockBean(LogoutHandler.class)
        }
)
public class WebMvcTestBase {
    protected final MockMvc mockMvc;

    public WebMvcTestBase(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
}
