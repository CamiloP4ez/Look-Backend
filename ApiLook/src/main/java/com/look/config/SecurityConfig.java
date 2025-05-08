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

//import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize, @PostAuthorize
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
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*")); 
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Aplicar a /api/**
        return source;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para APIs stateless
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Manejador de errores 401
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sesión stateless (JWT)
            .authorizeHttpRequests(auth -> auth
                // Endpoints Públicos
                .requestMatchers("/api/auth/**").permitAll() // Login y Register
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger

                // Endpoints de Posts (Ejemplos de permisos)
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll() // Cualquiera puede ver posts
                .requestMatchers(HttpMethod.POST, "/api/v1/posts").hasAnyRole("USER", "ADMIN", "SUPERADMIN") // Usuarios registrados pueden crear posts
                .requestMatchers(HttpMethod.PUT, "/api/v1/posts/{postId}").hasAnyRole("USER", "ADMIN", "SUPERADMIN") // Solo el autor o admins pueden editar? (Necesita lógica adicional o @PreAuthorize)
                .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/{postId}").hasAnyRole("ADMIN", "SUPERADMIN") // Admins pueden borrar posts (o el autor, necesita lógica)

                 // Endpoints de Comments (Ejemplos)
                .requestMatchers(HttpMethod.GET, "/api/v1/posts/{postId}/comments").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/posts/{postId}/comments").hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                // Endpoints de Likes (Ejemplos)
                 .requestMatchers(HttpMethod.POST, "/api/v1/posts/{postId}/like").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                 .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/{postId}/like").hasAnyRole("USER", "ADMIN", "SUPERADMIN")


                // Endpoints de Usuarios (Ejemplos de permisos más restrictivos)
                 .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAnyRole("ADMIN", "SUPERADMIN") // Solo admins ven la lista de usuarios
                 .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated() // El usuario autenticado puede ver su perfil
                 .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}").hasAnyRole("ADMIN", "SUPERADMIN") // Admins ven perfiles específicos
                 .requestMatchers(HttpMethod.PUT, "/api/v1/users/me").authenticated() // Usuario actualiza su perfil
                 .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/roles").hasRole("SUPERADMIN") // Solo SuperAdmin cambia roles
                 .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{userId}").hasRole("SUPERADMIN") // Solo SuperAdmin borra usuarios


                // Cualquier otra request requiere autenticación
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}