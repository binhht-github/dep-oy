package com.spring.filters;

import com.spring.security.oauth2.CustomOAuth2UserService;
import com.spring.security.oauth2.TokenProvider;
import com.spring.service.security.impl.JwtUserDetailsServiceImpl;
import com.spring.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUserDetailsServiceImpl jwtUserDetailsService;

    @Autowired
    private TokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("request : " + request.getRequestURI());
        String token = parseJwt(request);
        System.out.println("token :" + token);
        if (request.getRequestURI().contains("/api/v1/user/auth/user/me")) {
            try {

                System.out.println("jwt :" + token);

                if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                    Long userId = tokenProvider.getUserIdFromToken(token);

                    UserDetails userDetails = this.jwtUserDetailsService.loadUserById(userId);
//                    System.out.println("user :" + userDetails);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

//                    System.out.println("authen :" + authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ex) {
                logger.error("Could not set user authentication in security context", ex);
            }
        } else if (request.getRequestURI().contains("/api/v1/user/auth")) {
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
//                Long userId = tokenProvider.getUserIdFromToken(token);
                String email = this.tokenProvider.getEmailFromToken(token);
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER);
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        return null;
    }
}
