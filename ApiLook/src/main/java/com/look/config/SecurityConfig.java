package com.look.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.look.jwt.AuthEntryPointJwt;
import com.look.jwt.JwtAuthenticationFilter;
import com.look.service.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081","http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts").hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/posts/{postId}")
                        .hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/{postId}").hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/{postId}/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/{postId}/comments")
                        .hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/{postId}/like")
                        .hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/{postId}/like")
                        .hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/roles").hasRole("SUPERADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{userId}").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments").authenticated()

                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}