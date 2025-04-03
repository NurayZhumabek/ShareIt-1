package by.nuray.shareit.request;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
    public List<ItemRequestDTO> getRequests(@RequestHeader("X-Sharer-User-Id") int userId) {

        return requestService.getAllRequests(userId).
                stream()
                .map(request -> modelMapper.map(request, ItemRequestDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequestDTO> getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                         @PathVariable("id") int id) {
        ItemRequest request = requestService.getRequestById(id, userId);
        return ResponseEntity.ok(modelMapper.map(request, ItemRequestDTO.class));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable("id") int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody @Valid ItemRequestDTO itemRequestDTO,
                                           BindingResult bindingResult,
                                           @RequestHeader("X-Sharer-User-Id") int requestorId) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
        }

        ItemRequest itemRequest = modelMapper.map(itemRequestDTO, ItemRequest.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(requestService.createRequest(itemRequest, requestorId), ItemRequestDTO.class));

    }


    @GetMapping("/all")
    public List<ItemRequestDTO> getAllRequestsFromOthers(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {

        return requestService.getRequestsFromOthers(userId, from, size)
                .stream().map(request -> modelMapper.map(request, ItemRequestDTO.class))
                .collect(Collectors.toList());
    }

}
