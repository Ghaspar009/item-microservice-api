package com.task.api.items;

import com.task.api.items.dto.CreateItemRequest;
import com.task.api.items.dto.ItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTests {

    @Mock
    private ItemService itemService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ItemController itemController;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<String> loginCaptor;

    @Test
    void createItemShouldDelegateToServiceWithCorrectArguments() {
        //given
        CreateItemRequest request = new CreateItemRequest("newItem1");

        when(authentication.getName()).thenReturn("bob");

        //when
        itemController.createItem(request, authentication);

        //then
        verify(itemService).createItem(nameCaptor.capture(), loginCaptor.capture());
        assertEquals("newItem1", nameCaptor.getValue());
        assertEquals("bob", loginCaptor.getValue());
    }

    @Test
    void getItemShouldReturnListFromService() {
        //given
        when(authentication.getName()).thenReturn("bob");

        List<ItemResponse> expectedItems = List.of(
                new ItemResponse(UUID.randomUUID(), "newItem1"),
                new ItemResponse(UUID.randomUUID(), "newItem2")
        );

        when(itemService.getItemsForUser("bob")).thenReturn(expectedItems);

        //when
        List<ItemResponse> result = itemController.getItems(authentication);

        //then
        assertEquals(expectedItems, result);
        verify(itemService).getItemsForUser("bob");
    }
}
