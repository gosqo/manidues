package com.vong.manidues.filter;

import com.vong.manidues.utility.ResponseBodyWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 1. 액세스 토큰 없이 JwtAuthenticationFilter 를 거칠 때 호출 (페이지 최초 방문)
     * 2. 회원가입 시, validation 에러를 잡은 후, 추가적으로 이곳으로 호출이 이어지는 이유?
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        ResponseBodyWriter responseBodyWriter = new ResponseBodyWriter();

        if (authException instanceof BadCredentialsException) {
            responseBodyWriter.setResponseWithBody(
                    response,
                    400,
                    "아이디 혹은 비밀번호를 확인해주세요."
            );

        } else if (authException instanceof InsufficientAuthenticationException) {
            responseBodyWriter.setResponseWithBody(
                    response,
                    401,
                    "인증정보가 필요합니다."
            );
            log.info("""
                            *** Request to URI require authentication. *** response with {}
                                AuthHeader: {}
                            """
                    , response.getStatus()
                    , request.getHeader("Authorization")
            );
        }
    }
}
