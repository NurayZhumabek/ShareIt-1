package by.nuray.shareit.item;

import java.util.List;

public interface ItemService {

    public Item  createItem(Item item, int ownerId);

    public void updateItem(int id, Item item, int ownerId);

    public void deleteItem(int id,int userId);
    public Item getItemById(int id);
    public List<Item> getAllItems(int userId,int from, int size);
    public List<Item> searchItemsPaged(String itemName, int from, int size);

    List<Item> search(String itemName);

    public Item   createItemFromRequest(Item item, int ownerId, int requestId);

    public List<Item> getItemsByRequestId(int requestId);




    }
