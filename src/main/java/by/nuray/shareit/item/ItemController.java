package by.nuray.shareit.item;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<ItemDto> getItemsbyOwner(
            @RequestHeader("X-Sharer-User-Id") int ownerId) {

        return itemService.getItemsByOwner(ownerId)
                .stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable("id") int id) {
        return modelMapper.map(itemService.getById(id), ItemDto.class);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id,
                                           @RequestBody @Valid ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") int ownerId) {

        Item item = itemService.getById(id);
        itemService.update(id, modelMapper.map(itemDto, Item.class), ownerId);
        return new ResponseEntity(item, HttpStatus.OK);

    }


    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        List<Item> foundItems = itemService.searchItem(text);

        return foundItems
                .stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }


    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") int ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        itemService.save(item, ownerId);
        return new ResponseEntity(modelMapper.map(item, ItemDto.class), HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable int id) {
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }


}







