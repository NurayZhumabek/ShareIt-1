package by.nuray.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemService {


    public Item  createItem(Item item, int ownerId);

    public void updateItem(int id, Item item, int ownerId);

    public void deleteItem(int id);
    public Item getItemById(int id);
    public List<Item> getAllItems();
    public List<Item> getAllItemsByOwner(int ownerId);
    public List<Item> searchItems(String itemName);
    public Item   createItemFromRequest(Item item, int ownerId, int requestId);





    }
