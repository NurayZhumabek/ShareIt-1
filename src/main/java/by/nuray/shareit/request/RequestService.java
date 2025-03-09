package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;

import java.util.List;

public interface RequestService {
    ItemRequest createRequest(ItemRequest itemRequest,int requestorId);



    List<ItemRequest> getAllRequests();

    ItemRequest getRequestById(int id);

    void deleteRequest(int requestId);

}
