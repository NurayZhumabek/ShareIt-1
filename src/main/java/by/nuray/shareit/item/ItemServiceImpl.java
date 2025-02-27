package by.nuray.shareit.item;

import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.ItemNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemServiceImpl implements ItemService {


    Map<Integer, Item> items = new HashMap<Integer, Item>();


    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<Item> findItem(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item getById(int id) {
        return findItem(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }


    @Override
    public List<Item> getItemsByOwner(int ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwner() != null && i.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }


    @Override
    public void save(Item item, int ownerId) {
        int newId = items.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        item.setId(newId);
        User owner = userService.getById(ownerId);
        item.setOwner(owner);

        items.put(item.getId(), item);
    }

    @Override
    public void update(int id, Item updatedItem, int ownerId) {

        Item currentItem = items.get(id);

        if (currentItem != null && currentItem.getOwner().getId() == ownerId) {

            if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
                currentItem.setName(updatedItem.getName());
            }
            if (updatedItem.getDescription() != null && !updatedItem.getDescription().isBlank()) {
                currentItem.setDescription(updatedItem.getDescription());
            }
            if (currentItem.isAvailable() != updatedItem.isAvailable()) {
                currentItem.setAvailable(updatedItem.isAvailable());
            }
            items.put(id, currentItem);

        } else {
            throw new ItemNotFoundException("Item not found or given incorrect owner id");
        }
    }



    @Override

    public void delete(int id) {
        findItem(id).orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        items.remove(id);
    }


    @Override
    public List<Item> searchItem(String itemName) {

        String name= itemName.toLowerCase();

        return items.values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(name)
                        || i.getDescription().toLowerCase().contains(name))
                        && i.isAvailable())
                .collect(Collectors.toList());

    }
}





