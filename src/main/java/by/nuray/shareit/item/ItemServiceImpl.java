package by.nuray.shareit.item;

import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.request.RequestService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.ItemValidationException;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ItemServiceImpl implements ItemService {


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
        User user = userService.getUserById(ownerId);

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
    public void deleteItem(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (item.getOwner().getId() != userId) {
            throw new ItemValidationException("You are not allowed to delete this item");
        }

        itemRepository.deleteById(itemId);
    }


    @Override
    public Item getItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + id + " not found"));
    }

    @Override
    public List<Item> getAllItems(int userId, int from, int size) {
        userService.getUserById(userId);
        return itemRepository.findAllByOwnerIdPaged(userId, from, size);
    }


    @Override
    public List<Item> searchItemsPaged(String itemName, int from, int size) {
        if (itemName == null || itemName.isBlank()) {
            throw new ItemValidationException("The search string is required");
        }
        return itemRepository.searchAvailableItemsPaged(itemName, from, size);

    }

    @Override
    public List<Item> search(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new ItemValidationException("The search string is required");
        }
        return itemRepository.searchAvailableItems(itemName);
    }

    @Override
    public Item createItemFromRequest(Item item, int ownerId, int requestId) {

        ItemRequest request = requestService.findRequestById(requestId);
        Item newItem = createItem(item, ownerId);
        newItem.setRequest(request);

        return itemRepository.save(newItem);

    }

    @Override
    public List<Item> getItemsByRequestId(int requestId) {
        return itemRepository.findByRequestId(requestId);
    }
}





