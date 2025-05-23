package itmo.blps.controller;

import itmo.blps.dto.request.BalanceRequest;
import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.security.SecurityUtils;
import itmo.blps.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;
    private final SecurityUtils securityUtils;

    @PostMapping("/create-card")
    @PreAuthorize("hasAuthority('CREATE_CARD')")
    public CardResponse createCard(@RequestBody @Valid CardRequest cardRequest) {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return cardService.createCard(cardRequest, username);
    }

    @PostMapping("/top-up")
    @PreAuthorize("hasAuthority('TOP_UP_BALANCE')")
    public CardResponse topUpBalance(@RequestBody @Valid BalanceRequest balanceRequest) {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return cardService.topUpBalance(balanceRequest, username);
    }
}
