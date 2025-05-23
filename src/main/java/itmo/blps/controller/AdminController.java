package itmo.blps.controller;

import itmo.blps.dto.response.AdminResponse;
import itmo.blps.exceptions.InvalidRequestException;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.security.SecurityUtils;
import itmo.blps.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final SecurityUtils securityUtils;

    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('VIEW_ADMIN_REQUESTS')")
    public List<AdminResponse> getAllAdminRequests() {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return adminService.getAllAdminRequests();
    }

    @PostMapping("/create-request")
    @PreAuthorize("hasAuthority('CREATE_ADMIN_REQUEST')")
    public void createAdminRequest(HttpServletRequest request) {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        adminService.createAdminRequest(request);
    }

    @PutMapping("/approve/{adminRequestId}")
    @PreAuthorize("hasAuthority('APPROVE_ADMIN_REQUEST')")
    public void approveOnAdminRequest(@PathVariable @Valid String adminRequestId) {
        String username = securityUtils.getCurrentUser();
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
}
