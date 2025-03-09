package by.nuray.shareit.item;

import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.request.RequestService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.ItemValidationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public  class ItemServiceImpl implements ItemService {


    private final ItemRepository itemRepository;
    private final UserService userService;
    private final RequestService requestService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, RequestService requestService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.requestService = requestService;
    }

    @Override
    public Item createItem(Item item, int ownerId) {

        User user = userService.getUserById(ownerId);

        if (item == null) {
            throw new ItemValidationException("item cannot be null");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ItemValidationException("item name is required");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ItemValidationException("item description is required");
        }
        item.setAvailable(true);
        item.setOwner(user);

        return itemRepository.save(item);
    }

    @Override
    public void updateItem(int id, Item item, int ownerId) {

        Item currentItem = getItemById(id);
        if (currentItem.getOwner().getId() != ownerId) {
            throw new ItemValidationException("You are not allowed to update this item");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(currentItem);


    }

    @Override
    public void deleteItem(int id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found");
        }
        itemRepository.deleteById(id);

    }

    @Override
    public Item getItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(()->new ItemNotFoundException("Item with id " + id + " not found"));
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> getAllItemsByOwner(int ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> searchItems(String itemName) {
        return itemRepository.findAll()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(itemName.toLowerCase())
                || item.getDescription().toLowerCase().contains(itemName.toLowerCase()))
                        && item.getAvailable())
                .collect(Collectors.toList());

    }

    @Override
    public Item createItemFromRequest(Item item, int ownerId, int requestId) {

        ItemRequest request = requestService.getRequestById(requestId);
        Item newItem = createItem(item, ownerId);
        newItem.setRequest(request);

        return itemRepository.save(newItem);


    }
}





