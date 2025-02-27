package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.util.RequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;


@Component
public class RequestServiceImpl implements RequestService {


    Map<Integer, ItemRequest> requests = new HashMap<Integer, ItemRequest>();

    private final ItemService itemService;

    public RequestServiceImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest) {


        if (itemRequest == null || itemRequest.getName() == null || itemRequest.getName().isBlank()) {
            throw new RequestException("Item request cannot be null");
        }
        String searchedItem = itemRequest.getName();

        if (!itemService.searchItem(searchedItem).isEmpty()) {
            throw new RequestException(searchedItem + " already exists");
        }
        int newId = requests.keySet().stream().max(Integer::compareTo).orElse(0) + 1;

        itemRequest.setId(newId);
        itemRequest.setCreatedDate(LocalDate.now());
        itemRequest.setFound(false);
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;


    }

    @Override
    public void addItemToRequest(Item item, int itemRequestId, int ownerId) {

        ItemRequest itemRequest = requests.get(itemRequestId);

        if (itemRequest == null) {
            throw new RequestException("Request not found");
        }

        if (!item.getName().equalsIgnoreCase(itemRequest.getName())) {
            throw new RequestException("Item does not meet the request criteria");
        }


        itemService.save(item, ownerId);
        itemRequest.setFound(true);


    }


    @Override
    public List<ItemRequest> getRequests() {
        return new ArrayList<>(requests.values());
    }


    @Override
    public ItemRequest getRequestById(int id) {
        return requests.get(id);
    }
}
