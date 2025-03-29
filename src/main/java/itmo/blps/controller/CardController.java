package itmo.blps.controller;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping("/create-card")
    public CardResponse createCard(@RequestBody @Valid CardRequest cardRequest) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return cardService.createCard(cardRequest, username);
    }

    @PostMapping("/top-up")
    public CardResponse topUpBalance(@RequestParam String cardNumber, @RequestParam Double amount) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return cardService.topUpBalance(cardNumber, username, amount);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        return userDetails.getUsername();
    }
}
