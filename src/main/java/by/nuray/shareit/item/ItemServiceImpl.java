package by.nuray.shareit.item;

import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.ItemNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemServiceImpl implements ItemService {


    Map<Integer,Item> items = new HashMap<Integer,Item>();


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
        return findItem(id).orElseThrow(()->new ItemNotFoundException("Item not found"));
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
    public void update(int id, Item updatedItem,int ownerId) {

        Item item1 = items.get(id);

        if (item1 != null && item1.getOwner().getId() == ownerId) {
            updatedItem.setId(id);
            updatedItem.setOwner(item1.getOwner());
            items.put(id, updatedItem);
        }
        else {
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

       return items.values()
                .stream()
                .filter(i->i.getName().contains(itemName) && i.isAvailable())
                .collect(Collectors.toList());

        }
    }





