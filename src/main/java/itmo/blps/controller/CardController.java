package itmo.blps.controller;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.UserNotFoundException;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/card")
public class CardController {
    private final CardService cardService;

    @GetMapping("/get_all")
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards();
    }

    @PostMapping("/create")
    public CardResponse createCard(@RequestBody @Valid CardRequest cardRequest) {
        String username = getCurrentUser();
        return cardService.createCard(cardRequest, username);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
//            throw new IllegalStateException("no authentication");
        }
        return userDetails.getUsername();
    }
}
