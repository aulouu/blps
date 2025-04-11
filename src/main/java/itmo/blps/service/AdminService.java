package itmo.blps.service;

import itmo.blps.dto.response.AdminResponse;
import itmo.blps.exceptions.AdminRequestAlreadyExistException;
import itmo.blps.exceptions.AdminRequestNotFoundException;
import itmo.blps.exceptions.UserAlreadyAdminException;
import itmo.blps.exceptions.UserNotFoundException;
import itmo.blps.model.Admin;
import itmo.blps.model.Role;
import itmo.blps.model.User;
import itmo.blps.repository.AdminRepository;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    public List<AdminResponse> getAllAdminRequests() {
        List<Admin> adminRequests = adminRepository.findAll();
        return adminRequests.stream()
                .map(admin -> modelMapper.map(admin, AdminResponse.class))
                .collect(Collectors.toList());
    }

    public void approveOnAdminRequest(Long adminRequestId) {
        Admin adminRequest = adminRepository.findById(adminRequestId)
                .orElseThrow(() -> new AdminRequestNotFoundException(
                        String.format("Admin request not found %d", adminRequestId)));

        User user = adminRequest.getUser();
        if (user.getRole() == Role.ADMIN) {
            adminRepository.delete(adminRequest);
            throw new UserAlreadyAdminException(
                    String.format("User %s is already an admin", user.getUsername()));
        }
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        adminRepository.delete(adminRequest);
    }

    public void createAdminRequest(HttpServletRequest request) {
        User user = findUserByRequest(request);
        if (user.getRole() == Role.ADMIN)
            throw new UserAlreadyAdminException(
                    String.format("User %s is already an admin", user.getUsername()));
        if (adminRepository.existsByUser(user))
            throw new AdminRequestAlreadyExistException(
                    String.format("Admin request already exists from user %s", user.getUsername()));


        if (userRepository.findAllByRole(Role.ADMIN).isEmpty()) {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            return;
        }
        Admin adminRequest = new Admin();
        adminRequest.setUser(user);
        adminRepository.save(adminRequest);
    }

    private User findUserByRequest(HttpServletRequest request) {
        String username = jwtUtils.getUserNameFromJwtToken(jwtUtils.parseJwt(request));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)));
    }
}
