package itmo.blps.controller;

import itmo.blps.dto.response.AdminResponse;
import itmo.blps.exceptions.InvalidRequestException;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/requests")
    public List<AdminResponse> getAllAdminRequests() {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return adminService.getAllAdminRequests();
    }

    @PostMapping("/create-request")
    public void createAdminRequest(HttpServletRequest request) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        adminService.createAdminRequest(request);
    }

    @PutMapping("/approve/{adminRequestId}")
    public void approveOnAdminRequest(@PathVariable @Valid String adminRequestId) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        Long id;
        try {
            id = Long.parseLong(adminRequestId);
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Invalid admin request ID format");
        }
        adminService.approveOnAdminRequest(id);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        return userDetails.getUsername();
    }
}
