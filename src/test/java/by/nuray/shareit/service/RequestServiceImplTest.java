package by.nuray.shareit.service;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.request.ItemRequestRepository;
import by.nuray.shareit.request.RequestServiceImpl;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.RequestException;
import by.nuray.shareit.util.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {


    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;


    @InjectMocks
    RequestServiceImpl requestService;

    private ItemRequest itemRequest;
    private Item item;
    private User owner;
    private User requester;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setUsername("Test owner");
        owner.setEmail("owner@test.com");

        requester = new User();
        requester.setId(2);
        requester.setUsername("Test requester");
        requester.setEmail("requester@test.com");

        item = new Item();
        item.setName("test item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setId(1);

        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("description from request");
        itemRequest.setRequester(requester);

    }

    @Test
    public void getRequestById_ReturnsItem() {

        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(requester);

        Mockito.when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemService.getItemsByRequestId(Mockito.anyInt()))
                .thenReturn(Arrays.asList(item));

        ItemRequest result = requestService.getRequestById(1,1);

        assertNotNull(item);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals(item.getName(), result.getItems().get(0).getName());

    }

    @Test
    public void getRequestById_whenRequestNotFound_throwsRequestException(){
        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(requester);

        Mockito.when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RequestException.class,
                () -> requestService.getRequestById(1, 1));

        assertEquals("Request not found", exception.getMessage());
    }


    @Test
    public void getRequestById_whenUserNotFound_throwsUserNotFoundException(){

        Mockito.when(userService.getUserById(1))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));


       Exception exception= assertThrows(UserNotFoundException.class,
               () -> requestService.getRequestById(1, 1));

       assertEquals("User with id 1 not found", exception.getMessage());

    }

    @Test
    public void getRequestById_whenRequestHasNoItems_returnsRequestWithEmptyList() {
        Mockito.when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(requester);

        Mockito.when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemService.getItemsByRequestId(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        ItemRequest result = requestService.getRequestById(1, 1);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());
    }


    @Test
    public void getAllRequests_whenUserExistsAndHasRequestsWithItems_returnsRequestList() {

        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);
        Mockito.when(requestRepository.findAllByRequestorIdOrderByCreatedAtDesc(requester.getId()))
                .thenReturn(Arrays.asList(itemRequest));

        Mockito.when(itemService.getItemsByRequestId(Mockito.anyInt()))
                .thenReturn(Arrays.asList(item));


        List<ItemRequest> result = requestService.getAllRequests(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    public void getAllRequests_whenUserExistsAndHasNoRequests_returnsEmptyList(){
        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Mockito.when(requestRepository.findAllByRequestorIdOrderByCreatedAtDesc(requester.getId()))
                .thenReturn(Collections.emptyList());

        List<ItemRequest> result = requestService.getAllRequests(requester.getId());

        assertNotNull(result);
        assertEquals(0, result.size());

    }

    @Test
    public void getAllRequests_whenUserExistsAndRequestsHaveNoItems_returnsRequestsWithEmptyItems(){

        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Mockito.when(requestRepository.findAllByRequestorIdOrderByCreatedAtDesc(requester.getId()))
                .thenReturn(Arrays.asList(itemRequest));

        Mockito.when(itemService.getItemsByRequestId(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        List<ItemRequest> result = requestService.getAllRequests(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0,result.get(0).getItems().size());

    }

    @Test
    public void getAllRequests_whenUserNotFound_throwsUserNotFoundException(){
        Mockito.when(userService.getUserById(requester.getId()))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));


        Exception exception= assertThrows(UserNotFoundException.class,
                () -> requestService.getAllRequests(requester.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());
        Mockito.verify(requestRepository,Mockito.never()).findAllByRequestorIdOrderByCreatedAtDesc(requester.getId());

    }

    @Test
    public void getRequestsFromOthers_whenUserExists_returnsRequestsFromOthers(){

        User user = new User();
        user.setId(3);
        int from=1;
        int size=2;

        Mockito.when(userService.getUserById(user.getId()))
                .thenReturn(user);

        Mockito.when(requestRepository.findRequestsFromOthers(user.getId(),from,size))
                .thenReturn(Arrays.asList(itemRequest));

        List<ItemRequest> requests=requestService.getRequestsFromOthers(user.getId(), from,size);

        assertNotNull(requests);
        assertEquals(1, requests.size());

    }

    @Test
    public void getRequestsFromOthers_whenNoRequestsFromOthers_returnsEmptyList(){
        User user = new User();
        user.setId(3);
        int from=1;
        int size=2;

        Mockito.when(userService.getUserById(user.getId()))
                .thenReturn(user);

        Mockito.when(requestRepository.findRequestsFromOthers(user.getId(),from,size))
                .thenReturn(Collections.emptyList());

        List<ItemRequest> requests=requestService.getRequestsFromOthers(user.getId(), from,size);

        assertNotNull(requests);
        assertEquals(0, requests.size());

    }

    @Test
    public void getRequestsFromOthers_whenUserNotFound_throwsUserNotFoundException(){

        User user = new User();
        user.setId(3);
        int from=1;
        int size=2;

        Mockito.when(userService.getUserById(user.getId()))
                .thenThrow(new UserNotFoundException("User with id "+user.getId()+" not found"));


        Exception exception = assertThrows(UserNotFoundException.class,
                () -> requestService.getRequestsFromOthers(user.getId(), from,size));

        assertEquals("User with id 3 not found", exception.getMessage());

    }

    @Test
    public void findRequestById_whenRequestExists_returnsRequest(){
        Mockito.when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequest result = requestService.findRequestById(1);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    public void findRequestById_whenRequestNotFound_throwsRequestException(){
        Mockito.when(requestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(RequestException.class,
                () -> requestService.findRequestById(1));

        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    public void createRequest_whenValidInput_returnsSavedRequest(){


        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Mockito.when(itemService.search(itemRequest.getDescription()))
                .thenReturn(Collections.emptyList());

        Mockito.when(requestRepository.save(itemRequest)).thenReturn(itemRequest);


        ItemRequest request=requestService.createRequest(itemRequest,requester.getId());

        assertNotNull(request);

    }

    @Test
    public void createRequest_whenItemRequestIsNull_throwsRequestException(){

        itemRequest=null;

        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Exception exception= assertThrows(RequestException.class,
                () -> requestService.createRequest(itemRequest,requester.getId()));

        assertEquals("ItemRequest cannot be null", exception.getMessage());

        Mockito.verify(requestRepository,Mockito.never()).save(Mockito.any());
    }

    @Test
    public void createRequest_whenDescriptionIsNull_throwsRequestException(){

        itemRequest.setDescription(null);

        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Exception exception= assertThrows(RequestException.class,
                () -> requestService.createRequest(itemRequest,requester.getId()));

        assertEquals("Description cannot be null or empty", exception.getMessage());
        Mockito.verify(requestRepository,Mockito.never()).save(Mockito.any());

    }

    @Test
    public void createRequest_whenDescriptionIsBlank_throwsRequestException(){
        itemRequest.setDescription("");
        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Exception exception= assertThrows(RequestException.class,
                () -> requestService.createRequest(itemRequest,requester.getId()));

        assertEquals("Description cannot be null or empty", exception.getMessage());
        Mockito.verify(requestRepository,Mockito.never()).save(Mockito.any());

    }

    @Test
    public void createRequest_whenSimilarItemExists_throwsRequestException(){


        Mockito.when(userService.getUserById(requester.getId()))
                .thenReturn(requester);

        Mockito.when(itemService.search(itemRequest.getDescription()))
                .thenReturn(Arrays.asList(item));




       Exception exception =  assertThrows(RequestException.class,
               () -> requestService.createRequest(itemRequest,requester.getId()));

       assertEquals("Similar item is found, please try to search before creating a request", exception.getMessage());
    }

    @Test
    public void createRequest_whenUserNotFound_throwsUserNotFoundException(){

        Mockito.when(userService.getUserById(requester.getId()))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        Exception exception =  assertThrows(UserNotFoundException.class,
                () -> requestService.createRequest(itemRequest,requester.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());


    }

    @Test
    public void deleteRequest_whenRequestExists_deletesSuccessfully(){
        Mockito.when(requestRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);

        requestService.deleteRequest(itemRequest.getId());

        Mockito.verify(requestRepository,Mockito.times(1)).deleteById(itemRequest.getId());

    }

    @Test
    public void deleteRequest_whenRequestDoesNotExist_throwsRequestException(){

        Mockito.when(requestRepository.existsById(Mockito.anyInt()))
                .thenReturn(false);

        Exception exception=assertThrows(RequestException.class,
                () -> requestService.deleteRequest(itemRequest.getId()));

        assertEquals("Request not found", exception.getMessage());
        Mockito.verify(requestRepository,Mockito.never()).deleteById(Mockito.anyInt());
    }




































}
