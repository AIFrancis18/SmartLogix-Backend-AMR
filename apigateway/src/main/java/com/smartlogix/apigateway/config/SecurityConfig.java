package com.smartlogix.apigateway.config;

import com.smartlogix.apigateway.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // =========================
                // 🔓 PÚBLICO
                // =========================

                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers("/usuarios/login").permitAll()

                // =========================
                // 👑 USUARIOS
                // =========================

                .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                // =========================
                // 📦 PEDIDOS
                // =========================

                // Crear pedido
                .requestMatchers(HttpMethod.POST, "/pedidos")
                        .hasRole("OPERADOR")

                // Ver pedidos
                .requestMatchers(HttpMethod.GET, "/pedidos/**")
                        .hasAnyRole(
                                "ADMIN",
                                "OPERADOR",
                                "LOGISTICA",
                                "BODEGA"
                        )

                // Cambiar estado pedido
                .requestMatchers(HttpMethod.PUT, "/pedidos/**")
                        .hasRole("LOGISTICA")

                // =========================
                // 🚚 ENVÍOS
                // =========================

                // Ver envíos
                .requestMatchers(HttpMethod.GET, "/envios/**")
                        .hasAnyRole(
                                "ADMIN",
                                "LOGISTICA"
                        )

                // Crear envío
                .requestMatchers(HttpMethod.POST, "/envios")
                        .hasRole("LOGISTICA")

                // =========================
                // 🏪 INVENTARIO
                // =========================

                // Crear producto
                .requestMatchers(HttpMethod.POST, "/productos")
                        .hasRole("BODEGA")

                // Ver productos
                .requestMatchers(HttpMethod.GET, "/productos/**")
                        .hasAnyRole(
                                "ADMIN",
                                "BODEGA",
                                "OPERADOR",
                                "LOGISTICA"
                        )

                // Modificar producto
                .requestMatchers(HttpMethod.PUT, "/productos/**")
                        .hasRole("BODEGA")

                // Eliminar producto
                .requestMatchers(HttpMethod.DELETE, "/productos/**")
                        .hasRole("BODEGA")

                // =========================
                // 🔒 RESTO
                // =========================

                .anyRequest().authenticated()
            )
            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}