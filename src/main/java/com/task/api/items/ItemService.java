package com.task.api.items;

import com.task.api.user.User;
import com.task.api.items.dto.ItemResponse;
import com.task.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for item related operations.
 */
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * Creates and saves a new item for a given user login.
     * @param itemName the name of the new item
     * @param login of the item owner
     * @throws RuntimeException if the user is not found by their login
     */
    public void createItem(String itemName, String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Item item = new Item();
        item.setName(itemName);
        item.setOwner(user);

        itemRepository.save(item);
    }

    /**
     * Retrieves all items of a user with the given login
     * @param login of the user
     * @return list of {@link ItemResponse}
     * @throws RuntimeException if the user is not found by their login
     */
    public List<ItemResponse> getItemsForUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found."));

        List<Item> items = itemRepository.findByOwnerId(user.getId());
        return items.stream()
                .map(item -> new ItemResponse(item.getId(), item.getName()))
                .toList();
    }
}
