package com.vong.manidues.auth;

import com.vong.manidues.config.JwtService;
import com.vong.manidues.utility.JsonResponseBody;
import com.vong.manidues.utility.ServletRequestUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final ServletRequestUtility servletRequestUtility;

    @PostMapping("/authenticate")
    public ResponseEntity<Object> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("""
                        request to "/api/v1/auth/authenticate"
                            Email is: {}
                        """,
                request.getEmail()
        );
        AuthenticationResponse response = service.authenticate(request);
        return response.getAccessToken() != null
                ? ResponseEntity.status(200).body(response)
                : ResponseEntity.status(400).body(
                        JsonResponseBody.builder()
                                .statusCode(400)
                                .message("인증에 실패했습니다.")
                                .build()
                );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        log.info("""
                        request to /api/v1/auth/refresh-token
                            Email is: {}
                            token is: {}
                        """,
                servletRequestUtility.extractEmailFromHeader(request),
                request.getHeader("Authorization")
        );
        return ResponseEntity.ok(service.refreshToken(request, response));
    }

}
