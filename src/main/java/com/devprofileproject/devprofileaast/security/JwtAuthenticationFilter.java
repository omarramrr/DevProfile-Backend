package com.devprofileproject.devprofileaast.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devprofileproject.devprofileaast.domain.repository.UserRepository;

import java.io.IOException;

@Component // tell Spring to create and manage this class automatically
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    //Betakhod el jwtService w userDetailsService w te3mlhom save fel filter
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, UserRepository userRepository)
    {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");

        //check if jwt contain token wala laa
        //law mafehash skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) 
        {
           filterChain.doFilter(request, response);//skip authontication and continue request   
           return; 
        }

        //Remove "Bearer" from token
        String token = authHeader.substring(7);

        String username = null;
        try 
        {
           username = jwtService.extractUsername(token);
        } catch (Exception e) 
        {
            //skip authontication and continue request   
            filterChain.doFilter(request, response);
            return;
        }
        
        //Law galna username wel user mesh authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) 
        {
            //load user from DB
           UserDetails userDetails = userDetailsService.loadUserByUsername(username);
           
           //validate Token
           if (jwtService.isTokenValid(token, userDetails)) 
           {
               //create authentication object
               UsernamePasswordAuthenticationToken authToken =
               new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 

               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

               SecurityContextHolder.getContext().setAuthentication(authToken);
           }
           
        }

        filterChain.doFilter(request, response);
    }
    
}
