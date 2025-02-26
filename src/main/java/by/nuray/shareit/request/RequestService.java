package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    ItemRequest createRequest(ItemRequest itemRequest);


    void addItemToRequest(Item item, int itemRequestId, int ownerId);

    List<ItemRequest> getRequests();

    ItemRequest getRequestById(int id);
}
