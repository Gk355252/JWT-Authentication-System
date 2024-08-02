//package com.jwt.project.jwtauthentication.security;
//
//import java.io.IOException;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private org.slf4j.Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    @Autowired
//    private JwtHelper jwtHelper;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String requestHeader = request.getHeader("Authorization");
//        String path = request.getRequestURI();
//
//        // Allow public endpoints without JWT verification
//        if (isPublicEndpoint(path)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        logger.info("Authorization Header: {}", requestHeader);
//
//        String username = null;
//        String token = null;
//
//        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
//            token = requestHeader.substring(7);
//            try {
//                username = jwtHelper.getUsernameFromToken(token);
//                logger.info("Username from token: {}", username);
//            } catch (IllegalArgumentException e) {
//                logger.error("Error getting username from token", e);
//            } catch (ExpiredJwtException e) {
//                logger.warn("Token has expired", e);
//            } catch (MalformedJwtException e) {
//                logger.error("Invalid token format", e);
//            }
//        } else {
//            logger.warn("Bearer string not found in token");
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            try {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                if (jwtHelper.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    logger.info("User authenticated: {}", username);
//                } else {
//                    logger.warn("Token validation failed");
//                }
//            } catch (RuntimeException e) {
//                logger.error("User loading or token validation failed", e);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isPublicEndpoint(String path) {
//        return path.startsWith("/auth/reset-password") ||
//                path.startsWith("/auth/login") ||
//                path.startsWith("/auth/create-user") ||
//                path.startsWith("/auth/request-reset-password");
//    }
//}




package com.jwt.project.jwtauthentication.security;

import com.jwt.project.jwtauthentication.services.CustomUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("Invalid Header Value !! ");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Validation fails !!");
            }
        }

        filterChain.doFilter(request, response);
    }
}