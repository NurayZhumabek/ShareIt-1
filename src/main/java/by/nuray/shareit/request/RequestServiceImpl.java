package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.RequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class RequestServiceImpl implements RequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    public RequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
    }

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest,int requestorId) {

        User requestor = userService.getUserById(requestorId);

        if (itemRequest == null) {
            throw new RequestException("ItemRequest cannot be null");
        }
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new RequestException("Description cannot be null or empty");
        }

        itemRequest.setRequester(requestor);
        itemRequestRepository.save(itemRequest);


        return itemRequestRepository.save(itemRequest);
    }



    @Override
    public List<ItemRequest> getAllRequests() {
        return itemRequestRepository.findAll();
    }

    @Override
    public ItemRequest getRequestById(int id) {
        return itemRequestRepository.findById(id).orElseThrow(()-> new RequestException("Request not found"));
    }

    @Override
    public void deleteRequest(int requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new RequestException("Request not found");
        }
        itemRequestRepository.deleteById(requestId);
    }
}
