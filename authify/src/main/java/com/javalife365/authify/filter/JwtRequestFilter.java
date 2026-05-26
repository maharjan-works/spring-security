package com.javalife365.authify.filter;

import com.javalife365.authify.service.AppUserDetailsService;
import com.javalife365.authify.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtils jwtUtils;

    private static final List<String> PUBLIC_URLS =List.of("/login","/register","/send-reset_otp","/reset-password","/logout");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(PUBLIC_URLS.contains(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = null;
        String email = null;

        //1. check the authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
        }

        //2. If not found in header, check cookies
        if (jwt == null){
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie: cookies){
                    if ("jwt".equals(cookie.getName())){
                        jwt =  cookie.getValue();
                        break;
                    }
                }
            }
        }

        //3. validate the token and set security context
        if(jwt != null){
            email = jwtUtils.extractEmail(jwt);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails =appUserDetailsService.loadUserByUsername(email);
                if (jwtUtils.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request,response);
    }
}
