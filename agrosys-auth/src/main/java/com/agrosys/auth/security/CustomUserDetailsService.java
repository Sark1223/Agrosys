package com.agrosys.auth.security;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agrosys.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRepository.UserCustom user = userRepository.loadUserByUsername(username);
        if (user == null || user.getRolId() == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRolName()));

        JSONArray modules = new JSONArray(user.getModules());
        for (int i = 0; i < modules.length(); i++) {
            authorities.add(new SimpleGrantedAuthority("MODULE_" + modules.getString(i)));
        }
        
        
        // if ("SUPERADMIN".equals(user.getRolName())) {
        //     authorities.add(new SimpleGrantedAuthority("ROLE_MANAGEMENT"));
        //     authorities.add(new SimpleGrantedAuthority("MODULE_MANAGEMENT"));
        // }
        
        log.debug("Módulos obtenidos para rolId {}: {}", user.getRolId(), modules);
        
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities);
    }
}