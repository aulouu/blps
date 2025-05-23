package itmo.blps.security.jwt;

import itmo.blps.security.jaas.JaasAuthorityGranter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);
    private final JwtUtils jwtUtils;
    private final JaasAuthorityGranter jaasAuthorityGranter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            UserDetails userDetails = getUserDetails(request);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: { }", e);
        }

        filterChain.doFilter(request, response);
    }

    public UserDetails getUserDetails(HttpServletRequest request) {
        String jwt = parseJwt(request);

        if (jwt == null || !jwtUtils.validateJwtToken(jwt))
            return null;

        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        Set<String> authorities = jaasAuthorityGranter.grant(new Principal() {
            @Override
            public String getName() {
                return username;
            }
        });

        return User.withUsername(username)
                .password("")
                .authorities(authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .build();
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String bearerPrefix = "Bearer ";
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(bearerPrefix)) {
            return headerAuth.substring(bearerPrefix.length());
        }

        return null;
    }
}
