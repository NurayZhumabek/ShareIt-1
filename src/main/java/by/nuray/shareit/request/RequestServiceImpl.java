package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemRepository;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.RequestException;

import by.nuray.shareit.util.RequestNotFound;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class RequestServiceImpl implements RequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public RequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService, ItemService itemService, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }


    @Override
    public ItemRequest createRequest(ItemRequest itemRequest, int requestorId) {

        User requestor = userService.getUserById(requestorId);

        if (itemRequest == null) {
            throw new RequestException("ItemRequest cannot be null");
        }
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new RequestException("Description cannot be null or empty");
        }

        List<Item> check = itemService.search(itemRequest.getDescription());

        if (!check.isEmpty()) {
            throw new RequestException("Similar item is found, please try to search before creating a request");

        }

        itemRequest.setRequester(requestor);
        itemRequest.setCreatedAt(LocalDateTime.now());

        return itemRequestRepository.save(itemRequest);
    }


    @Override
    public List<ItemRequest> getAllRequests(int requestorId) {

        User requestor = userService.getUserById(requestorId);

        List<ItemRequest> requests =
                itemRequestRepository.findAllByRequestorIdOrderByCreatedAtDesc(requestorId);

        for (ItemRequest itemRequest : requests) {
            List<Item> itemsForRequest = itemService.getItemsByRequestId(itemRequest.getId());
            itemRequest.setItems(itemsForRequest);

        }
        return requests;

    }

    @Override
    public ItemRequest getRequestById(int id, int userId) {
        User requestor = userService.getUserById(userId);
        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFound("Request not found"));

        List<Item> items = itemService.getItemsByRequestId(id);
        request.setItems(items);

        return request;
    }

    @Override
    public void deleteRequest(int requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new RequestNotFound("Request not found");
        }
        itemRequestRepository.deleteById(requestId);
    }


    @Override
    public List<ItemRequest> getRequestsFromOthers(int userId, int from, int size) {
        userService.getUserById(userId);
        return itemRequestRepository.findRequestsFromOthers(userId, from, size);
    }


    @Override
    public ItemRequest findRequestById(int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestException("Request not found"));
    }
}
