package com.task.api.items;

import com.task.api.items.dto.ItemResponse;
import com.task.api.user.User;
import com.task.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Test
    void createItemShouldSaveItemForExistingUser() {
        // given
        String login = "bob";
        String itemName = "newItem1";

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setLogin(login);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));

        // when
        itemService.createItem(itemName, login);

        // then
        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();

        assertEquals(itemName, savedItem.getName());
        assertEquals(mockUser, savedItem.getOwner());
    }

    @Test
    void createItemShouldThrowExceptionWhenUserNotFound() {
        // given
        String login = "unknown";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemService.createItem("Item", login));

        assertEquals("User not found.", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemsForUserShouldReturnMappedItemResponses() {
        // given
        String login = "bob";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setLogin(login);

        Item item1 = new Item();
        item1.setId(UUID.randomUUID());
        item1.setName("newItem1");
        item1.setOwner(user);

        Item item2 = new Item();
        item2.setId(UUID.randomUUID());
        item2.setName("newItem2");
        item2.setOwner(user);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item1, item2));

        // when
        List<ItemResponse> result = itemService.getItemsForUser(login);

        // then
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
    }

    @Test
    void getItemsForUserShouldThrowExceptionWhenUserNotFound() {
        // given
        String login = "unknown";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // expect
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                itemService.getItemsForUser(login)
        );

        assertEquals("User not found.", ex.getMessage());
        verify(itemRepository, never()).findByOwnerId(any());
    }
}
