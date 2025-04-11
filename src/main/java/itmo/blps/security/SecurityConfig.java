package itmo.blps.security;

import itmo.blps.model.Permission;
import itmo.blps.security.jwt.JwtAuthEntryPoint;
import itmo.blps.security.jwt.JwtAuthTokenFilter;
import itmo.blps.security.service.AuthUserDetailsService;
import itmo.blps.security.service.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final AuthUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/invalidate-session").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/api/payment/pay").hasAuthority(Permission.PAY_ORDER.name())
                        .requestMatchers("/api/orders/set-address").permitAll()
                        .requestMatchers("/api/orders/confirm").hasAuthority(Permission.CONFIRM_ORDER.name())
                        .requestMatchers("/api/orders/add-product").permitAll()
                        .requestMatchers("/api/orders/get-paid-orders").hasAuthority(Permission.VIEW_ALL_PAID_ORDERS.name())
                        .requestMatchers("/api/orders/get-confirmed-orders").hasAuthority(Permission.VIEW_ALL_CONFIRMED_ORDERS.name())
                        .requestMatchers("/api/orders/get-current").permitAll()
                        .requestMatchers("/api/cards/top-up").hasAuthority(Permission.TOP_UP_BALANCE.name())
                        .requestMatchers("/api/cards/create-card").hasAuthority(Permission.CREATE_CARD.name())
                        .requestMatchers("/api/addresses/create-address").permitAll()
                        .requestMatchers("/api/addresses/get-user-addresses").hasAuthority(Permission.VIEW_CURRENT_ADDRESSES.name())
                        .requestMatchers("/api/addresses").hasAuthority(Permission.VIEW_ALL_ADDRESSES.name())
                        .requestMatchers("/api/products").permitAll()
                        .requestMatchers("/api/products/{productId}").permitAll()
                        .requestMatchers("/api/admin/requests").hasAuthority(Permission.VIEW_ADMIN_REQUESTS.name())
                        .requestMatchers("/api/admin/create-request").hasAuthority(Permission.CREATE_ADMIN_REQUEST.name())
                        .requestMatchers("/api/admin/approve/{adminRequestId}").hasAuthority(Permission.APPROVE_ADMIN_REQUEST.name())
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:24680"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "from", "size"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
