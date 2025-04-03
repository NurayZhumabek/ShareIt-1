package by.nuray.shareit.service;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemRepository;
import by.nuray.shareit.item.ItemServiceImpl;
import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.request.RequestService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.ItemValidationException;
import by.nuray.shareit.util.RequestException;
import by.nuray.shareit.util.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {


    @Mock
    ItemRepository itemRepository;

    @Mock
    UserService userService;

    @Mock
    RequestService requestService;

    @InjectMocks
    ItemServiceImpl itemService;

    private Item item;

    private User user;


    @BeforeEach
    public void setUp() {

        user = new User();
        user.setId(1);
        user.setUsername("Test User");
        user.setEmail("test@test.com");

        item = new Item();
        item.setName("test item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setId(1);

    }


    @Test
    public void getItemById_ReturnsItem() {

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Item item = itemService.getItemById(1);

        Assertions.assertNotNull(item);

    }

    @Test
    public void getItemById_whenItemNotFound_throwsException() {
        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(1));

    }

    @Test
    public void getAllItems_WhenItemsExist_ReturnsItemList() {

        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(user);
        Mockito.when(itemRepository.findAllByOwnerIdPaged(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Arrays.asList(item));

        List<Item> items = itemService.getAllItems(1, 1, 1);

        assertEquals(1, items.size());
        Assertions.assertNotNull(items);
    }


    @Test
    public void getAllItems_WhenNoItems_ReturnsEmptyList() {
        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(user);
        Mockito.when(itemRepository.findAllByOwnerIdPaged(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        List<Item> items = itemService.getAllItems(1, 1, 1);

        Assertions.assertTrue(items.isEmpty());
    }


    @Test
    public void createItem_whenValidInput_returnsSavedItem() {
        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(user);

        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        Item saveItem= itemService.createItem(item,1);


        Assertions.assertNotNull(saveItem);
        assertEquals("test item", saveItem.getName());
        assertEquals("description", saveItem.getDescription());
        assertEquals(user, saveItem.getOwner());

    }

    @Test
    public void createItem_whenUserIsNotExists_throwsException() {

        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(item,1));

    }

    @Test
    public void createItem_whenItemNameIsNull_ThrowsException(){
        item.setName(null);

        assertThrows(ItemValidationException.class,
                () -> itemService.createItem(item,1));
    }

    @Test
    public void createItem_whenItemNameIsBlank_ThrowsException() {
        item.setName(" ");

        assertThrows(ItemValidationException.class,
                () -> itemService.createItem(item,1));
    }

    @Test
    public void createItem_whenItemDescriptionIsNull_ThrowsException(){
        item.setDescription(null);

        assertThrows(ItemValidationException.class,
                () -> itemService.createItem(item,1));
    }

    @Test
    public void createItem_whenItemDescriptionIsBlank_ThrowsException() {
        item.setDescription(" ");

        assertThrows(ItemValidationException.class,
                () -> itemService.createItem(item,1));
    }


    @Test
    public  void whenUpdateItem_returnsUpdatedItem() {

        Item updated = new Item();
        updated.setName("Updated item");
        updated.setDescription("description");
        updated.setAvailable(false);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);

        Assertions.assertEquals("Updated item",item.getName());
        Assertions.assertEquals("description",item.getDescription());
        Assertions.assertFalse(item.getAvailable());
        Mockito.verify(itemRepository, Mockito.times(1)).save(item);

    }

    @Test
    public  void updateItem_whenItemIdIsNotExists_throwsException() {

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(1, item, 1));

        assertEquals("Item with id 1 not found", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));

    }

    @Test
    public  void updateItem_whenOwnerIdIsMisMatch_throwsException() {

        User anotherUser = new User();
        anotherUser.setId(2);


        Item updated = new Item();
        updated.setName("Updated item");
        updated.setDescription("description");
        updated.setAvailable(false);
        updated.setOwner(anotherUser);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));


        ItemValidationException exception= assertThrows(ItemValidationException.class,
                () -> itemService.updateItem(1, updated, 2));

        assertEquals("You are not allowed to update this item", exception.getMessage());

        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));

    }


    @Test
    public void updateItem_whenItemNameIsNull_doesNotUpdateItemName() {

        Item updated = new Item();
        updated.setName(null);
        updated.setDescription("upd description");
        updated.setAvailable(false);
        updated.setOwner(user);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);


        assertEquals("test item", item.getName()); //
        assertEquals("upd description", item.getDescription());
        Assertions.assertFalse(item.getAvailable());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

    }


    @Test
    public void updateItem_whenItemNameIsBlank_doesNotUpdateItemName() {
        Item updated = new Item();
        updated.setName(" ");
        updated.setDescription("upd description");
        updated.setAvailable(false);
        updated.setOwner(user);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);


        assertEquals("test item", item.getName()); //
        assertEquals("upd description", item.getDescription());
        Assertions.assertFalse(item.getAvailable());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

}

    @Test
    public void updateItem_whenItemDescriptionIsNull_doesNotUpdateDescription() {

        Item updated = new Item();
        updated.setName("updated item");
        updated.setDescription(null);
        updated.setAvailable(false);
        updated.setOwner(user);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);


        assertEquals("updated item", item.getName()); //
        assertEquals("description", item.getDescription());
        Assertions.assertFalse(item.getAvailable());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

    }


    @Test
    public void updateItem_whenItemDescriptionIsBlank_doesNotUpdateDescription() {
        Item updated = new Item();
        updated.setName("updated item");
        updated.setDescription(" ");
        updated.setAvailable(false);
        updated.setOwner(user);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);

        assertEquals("updated item", item.getName()); //
        assertEquals("description", item.getDescription());
        Assertions.assertFalse(item.getAvailable());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

    }

    @Test
    public void updateItem_whenAvailableIsNull_doesNotUpdateStatus() {

        item.setAvailable(true);


        Item updated = new Item();
        updated.setName("updated item");
        updated.setDescription("upd description");
        updated.setAvailable(null);
        updated.setOwner(user);

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        itemService.updateItem(1, updated, 1);

        assertEquals("updated item", item.getName()); //
        assertEquals("upd description", item.getDescription());
        Assertions.assertTrue(item.getAvailable());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));

    }


        @Test
        public void deleteItem_whenUserIsOwner_deletesSuccessfully() {

            item.setOwner(user);

            Mockito.when(itemRepository.findById(item.getId()))
                    .thenReturn(Optional.of(item));

            itemService.deleteItem(item.getId(), user.getId());

            Mockito.verify(itemRepository).findById(item.getId());
            Mockito.verify(itemRepository).deleteById(item.getId()); // 👈 вот здесь fix
            Mockito.verifyNoMoreInteractions(itemRepository);
        }


     @Test
     public void deleteItem_whenUserIsNotOwner_throwsException() {

     User anotherUser = new User();
     anotherUser.setId(2);

     item.setOwner(anotherUser);

     Mockito.when(itemRepository.findById(Mockito.anyInt()))
                    .thenReturn(Optional.of(item));

     ItemValidationException exception = assertThrows(ItemValidationException.class,
                () -> itemService.deleteItem(item.getId(), user.getId()));

     assertEquals("You are not allowed to delete this item",
                exception.getMessage());
     Mockito.verify(itemRepository, Mockito.never()).deleteById(item.getId());

    }


    @Test
    public void deleteItem_whenItemIdIsNotExists_throwsException() {


        Mockito.when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException= assertThrows(
                ItemNotFoundException.class,() -> itemService.deleteItem(item.getId(), user.getId()));

        assertEquals("Item not found", itemNotFoundException.getMessage());

        Mockito.verify(itemRepository, Mockito.never()).deleteById(Mockito.anyInt());


    }

    @Test
    public void searchItems_whenQueryMatchesItems_returnsList() {

        String query = "test item";
        Mockito.when(itemRepository.searchAvailableItems(query))
                .thenReturn(Arrays.asList(item));

        List<Item> items = itemService.search(query);

        Assertions.assertNotNull(items);
        assertEquals("test item", items.get(0).getName());
        Mockito.verify(itemRepository).searchAvailableItems(query);

    }

    @Test
    public void searchItems_whenQueryMatchesNothing_returnsEmptyList() {

        String query = "item";
        Mockito.when(itemRepository.searchAvailableItems(query))
                .thenReturn(Collections.emptyList());

        List<Item> items = itemService.search(query);

        assertEquals(0, items.size());
        Mockito.verify(itemRepository).searchAvailableItems(query);


    }


    @Test
    public void searchItems_whenItemNameIsNull_throwsException() {

        String query = null;

        Exception exception = assertThrows(ItemValidationException.class,
                () -> itemService.search(query));

        assertEquals("The search string is required", exception.getMessage());

    }

    @Test
    public void searchItems_whenItemNameIsBlank_throwsException() {

        String query = " ";

        Exception exception = assertThrows(ItemValidationException.class,
                () -> itemService.search(query));

        assertEquals("The search string is required", exception.getMessage());

    }

    @Test
    public void searchItemsPaged_whenQueryMatchesItems_returnsList() {
        String query = "test item";
        int from = 1;
        int size = 3;

        Item second = new Item();
        second.setName("test item");
        second.setDescription("test description");
        second.setAvailable(true);

        Item first = new Item();
        first.setName("test item");
        first.setDescription("test description");
        first.setAvailable(true);

        Mockito.when(itemRepository.searchAvailableItemsPaged(query,from,size))
                .thenReturn(Arrays.asList(first, second,item));

        List<Item> items = itemService.searchItemsPaged(query,from,size);

        Assertions.assertNotNull(items);

        assertAll(
                () -> assertEquals("test item", items.get(0).getName()),
                () -> assertEquals("test item", items.get(1).getName())
        );

    }

    @Test
    public void searchItemsPaged_whenQueryMatchesNothing_returnsEmptyList() {
        String query = "item";
        int from = 1;
        int size = 3;

        Mockito.when(itemRepository.searchAvailableItemsPaged(query,from,size))
                .thenReturn(Collections.emptyList());

        List<Item> items = itemService.searchItemsPaged(query,from,size);

        assertTrue(items.isEmpty());
        Mockito.verify(itemRepository).searchAvailableItemsPaged(query,from,size);

    }

    @Test
    public void searchItemsPaged_whenItemNameIsNull_throwsException() {

        String query = null;
        int from = 1;
        int size = 3;

        Exception exception = assertThrows(ItemValidationException.class,
                () -> itemService.searchItemsPaged(query,from,size));

        assertEquals("The search string is required", exception.getMessage());

    }

    @Test
    public void searchItemsPaged_whenItemNameIsBlank_throwsException() {

        String query = " ";
        int from = 1;
        int size = 3;

        Exception exception = assertThrows(ItemValidationException.class,
                () -> itemService.searchItemsPaged(query,from,size ));

        assertEquals("The search string is required", exception.getMessage());

    }

    @Test
    public void createItemFromRequest_whenValidInput_returnsSavedItem() {
        int requestId = 2;

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);

        Item addedItem = new Item();
        addedItem.setName("test item");
        addedItem.setDescription("test description");
        addedItem.setAvailable(true);
        addedItem.setOwner(user);

        Mockito.when(requestService.findRequestById(requestId))
                .thenReturn(itemRequest);

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Item result = itemService.createItemFromRequest(addedItem, user.getId(), requestId);

        assertNotNull(result);
        assertEquals("test item", result.getName());
        assertEquals(requestId, result.getRequest().getId());

        Mockito.verify(itemRepository, Mockito.times(2)).save(Mockito.any(Item.class));
    }


    @Test
    public void createItemFromRequest_whenRequestNotFound_throwsRequestException() {
        int requestId = 2;

        Item addedItem = new Item();
        addedItem.setName("test item");
        addedItem.setDescription("test description");
        addedItem.setAvailable(true);
        addedItem.setOwner(user);

        Mockito.when(requestService.findRequestById(requestId))
                .thenThrow(new RequestException("Request not found"));

        RequestException exception = assertThrows(RequestException.class,
                () -> itemService.createItemFromRequest(addedItem, user.getId(), requestId));

        assertEquals("Request not found", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }










}















