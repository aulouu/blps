package itmo.blps.security.service;

import itmo.blps.model.Permission;
import itmo.blps.model.Role;
import itmo.blps.model.User;
import itmo.blps.repository.RolePermissionRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Set<Role> roles = new HashSet<>();

        if (user.getRole() != null) {
            roles.add(user.getRole());
        }

        Set<Permission> permissions = new HashSet<>();
        for (Role role : roles) {
            permissions.addAll(rolePermissionRepository.findPermissionsByRole(role));
        }

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name())));

        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}
