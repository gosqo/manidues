package com.vong.manidues.config.trackingip;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistedIpsFilter extends OncePerRequestFilter {

    @Value("${blacklisted-ips-list}")
    private List<String> blacklistedIps;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestedIpAddress = request.getRemoteAddr();
        String requestedUserAgent = request.getHeader("User-Agent");

        if (requestedUserAgent == null ||
                requestedUserAgent.equals("null") ||
                requestedUserAgent.isBlank() ||
                requestedUserAgent.isEmpty()
        ) {
            log.warn("""


                        Request from User-Agent null or empty. IP address is: {}
                    requested User-Agent is: {}
                    """, requestedIpAddress, requestedUserAgent);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (blacklistedIps.contains(requestedIpAddress)) {
            log.warn("""


                        Request from blacklisted ip. IP address is: {}
                    requested User-Agent is: {}
                    listed size is: {}
                    """,
                    requestedIpAddress,
                    requestedUserAgent,
                    blacklistedIps.size()
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);

    }
}
