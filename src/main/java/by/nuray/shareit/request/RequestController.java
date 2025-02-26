package by.nuray.shareit.request;


import by.nuray.shareit.item.Item;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final ModelMapper modelMapper;

    public RequestController(RequestService requestService, ModelMapper modelMapper) {
        this.requestService = requestService;
        this.modelMapper = modelMapper;
    }


    @GetMapping
    public List<ItemRequestDTO> getRequests() {
        return requestService.getRequests()
                .stream()
                .map(request-> modelMapper.map(request, ItemRequestDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ItemRequestDTO> createRequest(@RequestBody ItemRequest request) {
        requestService.createRequest(request);
        return ResponseEntity.ok(modelMapper.map(request, ItemRequestDTO.class));

    }

    @PatchMapping("/{id}/response")
    public ResponseEntity<ItemRequestDTO> updateRequest(@PathVariable int id,@RequestBody Item item,
                                                        @RequestHeader(  "X-Sharer-User-Id") int ownerId) {
        requestService.addItemToRequest(item, id,ownerId);
        ItemRequest updatedRequest = requestService.getRequestById(id);

        return ResponseEntity.ok(modelMapper.map(updatedRequest, ItemRequestDTO.class));
    }
}
