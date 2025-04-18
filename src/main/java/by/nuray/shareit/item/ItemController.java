package by.nuray.shareit.item;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ModelMapper modelMapper;
    private final ItemService itemService;

    public ItemController(ModelMapper modelMapper, ItemService itemService) {
        this.modelMapper = modelMapper;
        this.itemService = itemService;
    }


    @GetMapping
    public List<ItemDto> getItems(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return itemService.getAllItems(userId, from, size)
                .stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable("id") int id) {
        return modelMapper.map(itemService.getItemById(id), ItemDto.class);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable int id,
                                        @RequestBody @Valid ItemDto itemDto,
                                        BindingResult bindingResult,
                                        @RequestHeader("X-Sharer-User-Id") int ownerId) {

        Item updated = modelMapper.map(itemDto, Item.class);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        itemService.updateItem(id, updated, ownerId);

        return ResponseEntity.ok(modelMapper.map(updated, ItemDto.class));


    }


    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam @NotBlank String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        List<Item> foundItems = itemService.searchItemsPaged(text, from, size);

        return foundItems
                .stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }


    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody @Valid ItemDto itemDto,
                                        BindingResult bindingResult,
                                        @RequestHeader("X-Sharer-User-Id") int ownerId) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        Item item = modelMapper.map(itemDto, Item.class);

        Item savedItem;
        if (itemDto.getRequestId() != null) {
            savedItem = itemService.createItemFromRequest(item, ownerId, itemDto.getRequestId());
        } else {
            savedItem = itemService.createItem(item, ownerId);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(savedItem, ItemDto.class));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable int id,
                                        @RequestHeader("X-Sharer-User-Id") int userId) {
        itemService.deleteItem(id, userId);
        return ResponseEntity.noContent().build();
    }


}







