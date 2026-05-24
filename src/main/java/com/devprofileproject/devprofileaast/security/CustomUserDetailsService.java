package com.devprofileproject.devprofileaast.security;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import com.devprofileproject.devprofileaast.domain.repository.UserRepository;

@Service //bet3ml create lel class automatic w bet3mlo manage
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // hena ana baaol lel spring shaghal UserRepository 
    //w el spring bey3mlo automatic
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
