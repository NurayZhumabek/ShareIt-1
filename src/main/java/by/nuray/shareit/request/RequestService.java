package by.nuray.shareit.request;


import java.util.List;

public interface RequestService {
    ItemRequest createRequest(ItemRequest itemRequest, int requestorId);

    List<ItemRequest> getAllRequests(int requestorId);

    ItemRequest getRequestById(int id, int userId);

    void deleteRequest(int requestId);

    List<ItemRequest> getRequestsFromOthers(int userId, int from, int size);

    ItemRequest findRequestById(int requestId);

}
