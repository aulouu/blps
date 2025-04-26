package itmo.blps.security;

import blps.jca.bitrix24_adapter.Bitrix24ConnectionFactory;
import blps.jca.bitrix24_adapter.Bitrix24ManagedConnectionFactory;
import itmo.blps.exceptions.CustomAccessDeniedHandler;
import itmo.blps.model.Permission;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.jaas.JaasAuthorityGranter;
import itmo.blps.security.jaas.JaasLoginModule;
import itmo.blps.security.jwt.JwtAuthEntryPoint;
import itmo.blps.security.jwt.JwtAuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
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

import javax.security.auth.login.AppConfigurationEntry;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthTokenFilter jwtAuthTokenFilter;
    private final UserRepository userRepository;
    @Value("${jca.defaultWebhookUrl}")
    private String defaultWebhookUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(jaasAuthenticationProvider(configuration()))
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
    public InMemoryConfiguration configuration() {
        AppConfigurationEntry configEntry = new AppConfigurationEntry(JaasLoginModule.class.getName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                Map.of("userRepository", userRepository));
        var configurationEntries = new AppConfigurationEntry[]{configEntry};
        return new InMemoryConfiguration(Map.of("SPRINGSECURITY", configurationEntries));
    }

    @Bean
    @Qualifier
    public AuthenticationProvider jaasAuthenticationProvider(javax.security.auth.login.Configuration configuration) {
        var provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(configuration);
        provider.setAuthorityGranters(new AuthorityGranter[]{new JaasAuthorityGranter(userRepository)});
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public Bitrix24ConnectionFactory bitrix24ConnectionFactory() {
        Bitrix24ManagedConnectionFactory mcf = new Bitrix24ManagedConnectionFactory();
        mcf.setDefaultWebhookUrl(defaultWebhookUrl);
        return new Bitrix24ConnectionFactory(mcf, null);
    }

}
