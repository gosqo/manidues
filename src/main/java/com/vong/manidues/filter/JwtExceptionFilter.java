package com.vong.manidues.filter;

import com.vong.manidues.token.JwtService;
import com.vong.manidues.utility.AuthHeaderUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthHeaderUtility authHeaderUtility;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        if (authHeaderUtility.isNotAuthenticated(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwToken = authHeaderUtility.extractJwtFromHeader(request);

        if (throwAnyJwtException(response, jwToken)) return;

        filterChain.doFilter(request, response);
    }

    private boolean throwAnyJwtException(
            HttpServletResponse response
            , String jwToken
    ) throws IOException {

        try {
            jwtService.extractUserEmail(jwToken);
        } catch (ExpiredJwtException ex) {
            log.debug("expired token, normal user will request to POST /api/v1/auth/refresh-token");
            response.sendError(401, "토큰 만료.");

            return true;
        } catch (JwtException ex) {
            log.warn("""
                            *** manipulated token *** response with 400. {}: {}
                            tried token: {}"""
                    , ex.getClass().getName()
                    , ex.getMessage()
                    , jwToken
            );
            response.sendError(400, "올바른 접근이 아닙니다. 로그아웃 후 다시 로그인 해주십시오.");

            return true;
        }
        return false;
    }
}
