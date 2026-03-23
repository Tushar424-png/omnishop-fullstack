package com.Ecommerce.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class EcommerceConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public EcommerceConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth

                // 🌍 PUBLIC APIs
                .requestMatchers(
                    "/user/register",
                    "/user/login",
                    "/products/getall",
                    "/products/getone/**",
                    "/api/gemini/ask",
                    "/Inventory/add",
                    "/user/getone/**",
                    "/api/gemini/ask",
                    "/products/add"
                ).permitAll()

                // 👤 USER APIs
                .requestMatchers(
                        "/Cart/**",
                        "/wishlist/**",
                        "/order/create/**",
                        "/order/check/**",
                        "/user/save-token" ,
                        "user/subscribe"// 👈 YEH ADD KARO
                ).hasAuthority("USER")

                // 👑 ADMIN APIs
                .requestMatchers(
                    "/Inventory/Restock",
                    "/Inventory/ReduceQuantity",
                    "/user/getall",
                    "/order/getall",
                    "/Inventory/getall"
                ).hasAuthority("ADMIN")

                .anyRequest().authenticated()
            );

        return http.build();
    }

    // ⭐ CORS CONFIG
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
    List.of(
        "http://localhost:3000",
        "https://omnishop-frontend.netlify.app"
    )
);;
        configuration.setAllowedMethods(
                List.of("GET","POST","PUT","DELETE","OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
