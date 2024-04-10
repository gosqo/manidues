package com.vong.manidues.config.trackingip;

import com.vong.manidues.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AbnormalRequestFilter extends OncePerRequestFilter {

    private final String[] WHITE_LIST_USER_AGENTS = {
            "mozilla"
            , "postman"
    };

    private boolean isWhiteListUserAgent(String userAgent) {
        for (String whiteUserAgent : WHITE_LIST_USER_AGENTS) {
            if (userAgent.toLowerCase().contains(whiteUserAgent)) return true;
        }
        return false;
    }

    private boolean isAbnormalUserAgent(String userAgent) {
        return userAgent == null
                || userAgent.isBlank()
                || !isWhiteListUserAgent(userAgent);
    }

    private boolean isAbnormalConnection(String connection) {
        return connection == null
                || !connection.equalsIgnoreCase("keep-alive");
    }

    // 허용된 uri 에 대한 요청인가
    private boolean isUnregisteredURI(String requestURI, String[] whiteListURIs) {
        if (requestURI.equals("/")) return false;

        for (String registeredURI : whiteListURIs) {
            if (registeredURI.equals("/")) continue;
            if (registeredURI.endsWith("/**"))
                registeredURI = registeredURI.substring(0, registeredURI.lastIndexOf("/"));
            if (requestURI.startsWith(registeredURI)) return false;
        }

        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestProtocol = request.getProtocol();
        String requestIp = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String connection = request.getHeader("Connection");
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {
            if (requestURI.startsWith("/css/")
                    || requestURI.startsWith("/js/")
                    || requestURI.startsWith("/img/")
            ) {
                filterChain.doFilter(request, response);
                return;
            }

            if (isAbnormalUserAgent(userAgent)
                    || isAbnormalConnection(connection)
            ) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.warn("""

                                    *** Request from not allowed UA or Connection *** response with {}
                                    {} {} {} {}
                                    User-Agent: {}
                                    Connection: {}
                                """
                        , response.getStatus()
                        , requestIp
                        , requestProtocol
                        , requestMethod
                        , requestURI
                        , userAgent
                        , connection
                );
                return;
            }

            // 허용된 uri 에 대한 요청이 아니라면
            if (requestMethod.equalsIgnoreCase("get")) {
                if (isUnregisteredURI(
                        requestURI,
                        SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_GET)
                ) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    log.warn("""

                                        *** Request to URI not permitted all *** response with {}
                                        {} {} {} {}
                                    """
                            , response.getStatus()
                            , requestIp
                            , requestProtocol
                            , requestMethod
                            , requestURI
                    );
                    return;
                }
            }

            if (requestMethod.equalsIgnoreCase("post")) {
                if (isUnregisteredURI(
                        requestURI,
                        SecurityConfig.WHITE_LIST_URIS_NON_MEMBER_POST)
                ) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    log.warn("""

                                        *** Request to URI not permitted all *** response with {}
                                        {} {} {} {}
                                    """
                            , response.getStatus()
                            , requestIp
                            , requestProtocol
                            , requestMethod
                            , requestURI
                    );
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}