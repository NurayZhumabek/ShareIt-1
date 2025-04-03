package by.nuray.shareit.controller;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemController;
import by.nuray.shareit.item.ItemDto;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.ItemValidationException;
import by.nuray.shareit.util.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1);
        item.setName("test");
        item.setDescription("description");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setName("test");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

    }

    private void mockMappingItemToItemDTO(ItemDto dto) {
        Mockito.when(modelMapper.map(Mockito.any(Item.class), Mockito.eq(ItemDto.class)))
                .thenReturn(dto);
    }

    private void mockMappingItemDtoItem(Item item) {
        Mockito.when(modelMapper.map(Mockito.any(ItemDto.class), Mockito.eq(Item.class)))
                .thenReturn(item);
    }

    @Test
    public void getItems_whenUserExistsAndItemsExist_returnsItemList() throws Exception {

        Mockito.when(itemService.getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(item));

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items")
                        .header("X-Sharer-User-Id", 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void getItems_whenUserExistsAndHasNoItems_returnsEmptyList() throws Exception {

        Mockito.when(itemService.getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items")
                        .header("X-Sharer-User-Id", 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        Mockito.verify(itemService, Mockito.times(1))
                .getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void getItems_whenUserNotFound_returns404() throws Exception {

        Mockito.when(itemService.getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items")
                        .header("X-Sharer-User-Id", 1)))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void getItemById_whenItemExists_returnsItemDTO() throws Exception {

        Mockito.when(itemService.getItemById(Mockito.anyInt())).thenReturn(item);

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemById(Mockito.anyInt());


    }

    @Test
    public void getItemById_whenItemNotFound_returns404() throws Exception {

        Mockito.when(itemService.getItemById(Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("Item with id 1 not found"));

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items/1")))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .getItemById(Mockito.anyInt());
    }

    @Test
    public void updateItem_whenValidInputAndOwner_updatesItemSuccessfully() throws Exception {

        itemDto.setName("new test");

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(Mockito.anyInt(), Mockito.any(Item.class), Mockito.anyInt());
    }


    @Test
    public void updateItem_whenInvalidInput_returns400() throws Exception {
        itemDto.setName(" ");

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).updateItem(Mockito.anyInt(),
                Mockito.any(Item.class), Mockito.anyInt());

    }

    @Test
    public void updateItem_whenUserIsNotOwner_returns400() throws Exception {

        Mockito.doThrow(new ItemValidationException("You are not allowed to update this item"))
                .when(itemService).updateItem(Mockito.anyInt(), Mockito.any(Item.class),
                        Mockito.anyInt());

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(Mockito.anyInt(), Mockito.any(Item.class), Mockito.anyInt());


    }

    @Test
    public void updateItem_whenItemNotFound_returns404() throws Exception {

        Mockito.doThrow(new ItemNotFoundException("Item with id 1 not found"))
                .when(itemService).updateItem(Mockito.anyInt(), Mockito.any(Item.class), Mockito.anyInt());

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(Mockito.anyInt(), Mockito.any(Item.class), Mockito.anyInt());

    }

    @Test
    public void searchItem_whenTextIsProvided_returnsMatchingItems() throws Exception {

        String text = "test";
        int from = 1;
        int size = 2;

        Mockito.when(itemService.searchItemsPaged(text, from, size))
                .thenReturn(List.of(item));

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items/search"))
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .searchItemsPaged(text, from, size);

    }

    @Test
    public void searchItem_whenTextIsBlank_returnsEmptyList() throws Exception {

        String text = " ";
        int from = 1;
        int size = 2;

        mockMappingItemToItemDTO(itemDto);

        mockMvc.perform((get("/items/search"))
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());


        Mockito.verify(itemService, Mockito.never()).searchItemsPaged(text, from, size);
    }

    @Test
    public void createItem_whenValidInputWithoutRequest_createsItem() throws Exception {


        Mockito.when(itemService.createItem(Mockito.any(Item.class), Mockito.anyInt())).thenReturn(item);

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .createItem(Mockito.any(Item.class), Mockito.anyInt());
    }

    @Test
    public void createItem_whenValidInputWithRequest_createsItemFromRequest() throws Exception {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        item.setRequest(itemRequest);

        itemDto.setRequestId(1);

        Mockito.when(itemService.createItemFromRequest(Mockito.any(Item.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(item);

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        Mockito.verify(itemService, Mockito.times(1))
                .createItemFromRequest(Mockito.any(Item.class), Mockito.anyInt(), Mockito.anyInt());
    }


    @Test
    public void createItem_whenInvalidInput_returns400() throws Exception {

        itemDto.setName(" ");

        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.never()).createItem(Mockito.any(Item.class), Mockito.anyInt());
    }

    @Test
    public void createItem_whenUserNotFound_returns404() throws Exception {

        Mockito.when(itemService.createItem(Mockito.any(Item.class), Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("Item with id 1 not found"));


        mockMappingItemToItemDTO(itemDto);
        mockMappingItemDtoItem(item);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .createItem(Mockito.any(Item.class), Mockito.anyInt());
    }

    @Test
    public void deleteItem_whenUserIsOwner_deletesSuccessfully() throws Exception {


        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNoContent());

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItem(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void deleteItem_whenItemNotFound_returns404() throws Exception {

        Mockito.doThrow(new ItemNotFoundException("Item not found"))
                .when(itemService).deleteItem(Mockito.anyInt(), Mockito.anyInt());


        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItem(Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void deleteItem_whenUserIsNotOwner_returns400() throws Exception {

        Mockito.doThrow(new ItemValidationException("You are not allowed to delete this item"))
                .when(itemService).deleteItem(Mockito.anyInt(), Mockito.anyInt());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItem(Mockito.anyInt(), Mockito.anyInt());

    }
}












