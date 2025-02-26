package by.nuray.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    public Optional<Item> findItem(int id);

    public Item getById(int  id);

    void save(Item item, int ownerId);

    public void update(int id, Item item, int ownerId);
    public void delete(int  id);
    public List<Item> searchItem(String itemName);

    public List<Item> getItemsByOwner(int ownerId);




}
