
package com.look.service;

import org.apache.coyote.BadRequestException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.look.dto.AuthLoginRequestDto;
import com.look.dto.AuthRegisterRequestDto;
import com.look.dto.AuthResponseDto;
import com.look.entity.Role;
import com.look.entity.User;
import com.look.exception.InternalServerErrorException;
import com.look.jwt.JwtTokenProvider;
import com.look.mapper.UserMapper;
import com.look.repository.RoleRepository;
import com.look.repository.UserRepository;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service 
public class AuthServiceImpl implements AuthService { 

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserMapper userMapper;
    
    @Autowired
    RoleRepository roleRepository;

    @Override 
    @Transactional
    public AuthResponseDto loginUser(AuthLoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                 () -> new BadCredentialsException("User details not found after authentication") 
            );

            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            return new AuthResponseDto(jwt, user.getId(), user.getUsername(), user.getEmail(), roleNames);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override 
    @Transactional
    public User registerUser(AuthRegisterRequestDto registerRequest) throws BadRequestException { 
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        User user = userMapper.registerDtoToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(new Date());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new InternalServerErrorException("Error: Default role ROLE_USER not found. Database seeding might be required.")); // El seeder debe asegurar que exista

        Set<Role> roles = new HashSet<>();
        roles.add(userRole); 
        user.setRoles(roles);

        return userRepository.save(user);
    }
}