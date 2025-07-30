package com.task.api.items;

import com.task.api.items.dto.CreateItemRequest;
import com.task.api.items.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users' items.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Creates a new item for the authenticated user.
     * @param request contains new item's name
     * @param authentication object providing the user's login
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createItem(@RequestBody CreateItemRequest request, Authentication authentication) {
        String login = authentication.getName();
        itemService.createItem(request.getName(), login);
    }

    /**
     * Retrieves all items owned by authenticated user.
     * @param authentication object providing the user's login
     * @return list of items owned by user
     */
    @GetMapping
    public List<ItemResponse> getItems(Authentication authentication) {
        String login = authentication.getName();
        return  itemService.getItemsForUser(login);
    }
}