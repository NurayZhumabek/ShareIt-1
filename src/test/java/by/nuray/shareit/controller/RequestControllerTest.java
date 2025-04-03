package by.nuray.shareit.controller;



import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.request.ItemRequestDTO;
import by.nuray.shareit.request.RequestController;
import by.nuray.shareit.request.RequestService;
import by.nuray.shareit.util.RequestException;
import by.nuray.shareit.util.RequestNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestService requestService;


    private ItemRequest itemRequest;
    private ItemRequestDTO itemRequestDTO;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Need a drill");

        itemRequestDTO = new ItemRequestDTO();
        itemRequestDTO.setId(1);
        itemRequestDTO.setDescription("Need a drill");

    }

    private void mockMappingReqToReqDTO(ItemRequestDTO dto) {
        Mockito.when(modelMapper.map(Mockito.any(ItemRequest.class), Mockito.eq(ItemRequestDTO.class)))
                .thenReturn(dto);
    }

    private void mockMappingReqDtoReq(ItemRequest request) {
        Mockito.when(modelMapper.map(Mockito.any(ItemRequestDTO.class), Mockito.eq(ItemRequest.class)))
                .thenReturn(request);
    }

    @Test
    public void createRequest_whenValidInput_returns201() throws Exception {

        Mockito.when(requestService.createRequest(Mockito.any(ItemRequest.class),Mockito.anyInt()))
                .thenReturn(itemRequest);

        mockMappingReqDtoReq(itemRequest);
        mockMappingReqToReqDTO(itemRequestDTO);

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()));

        Mockito.verify(requestService,Mockito.times(1)).createRequest(Mockito.any(ItemRequest.class),Mockito.anyInt());
    }

    @Test
    public void createRequest_whenMissingHeader_returns400() throws Exception {

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(requestService,Mockito.never()).createRequest(Mockito.any(ItemRequest.class),Mockito.anyInt());

    }

    @Test
    public void createRequest_whenInvalidHeader_returns400() throws Exception {


        itemRequestDTO.setDescription("");
        mockMappingReqDtoReq(itemRequest);
        mockMappingReqToReqDTO(itemRequestDTO);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isBadRequest());


        Mockito.verify(requestService,Mockito.never()).createRequest(Mockito.any(ItemRequest.class),Mockito.anyInt());

    }

    @Test
    public void getRequests_whenRequestsExist_returns200WithList() throws Exception {
        Mockito.when(requestService.getAllRequests(Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));


        mockMappingReqToReqDTO(itemRequestDTO);

        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDTO.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDTO.getDescription()));

        Mockito.verify(requestService,Mockito.times(1)).getAllRequests(Mockito.anyInt());

    }


    @Test
    public void getRequests_whenNoRequests_returns200WithEmptyList() throws Exception {
        Mockito.when(requestService.getAllRequests(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());


        mockMappingReqToReqDTO(itemRequestDTO);
        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        Mockito.verify(requestService,Mockito.times(1)).getAllRequests(Mockito.anyInt());

    }

    @Test
    public void getRequestById_whenValidUserAndRequestExists_returns200() throws Exception {

        Mockito.when(requestService.getRequestById(Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(itemRequest);

        mockMappingReqToReqDTO(itemRequestDTO);

        mockMvc.perform(get("/requests/1")
                .header("X-Sharer-User-Id",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDTO.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDTO.getDescription()));

        Mockito.verify(requestService,Mockito.times(1)).getRequestById(Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void getRequestById_whenRequestNotFound_returns404() throws Exception {
        Mockito.when(requestService.getRequestById(Mockito.anyInt(),Mockito.anyInt()))
                .thenThrow(new RequestNotFound("Request not found"));

        mockMappingReqToReqDTO(itemRequestDTO);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id",1))
                        .andExpect(status().isNotFound());

        Mockito.verify(requestService,Mockito.times(1)).getRequestById(Mockito.anyInt(),Mockito.anyInt());
    }

    @Test
    public void deleteRequest_whenRequestExists_returns204() throws Exception {

        mockMvc.perform(delete("/requests/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(requestService,Mockito.times(1)).deleteRequest(Mockito.anyInt());

    }

    @Test
    public void deleteRequest_whenRequestNotFound_returns404() throws Exception {

        Mockito.doThrow(new RequestNotFound("Request not found"))
                .when(requestService).deleteRequest(Mockito.anyInt());

        mockMvc.perform(delete("/requests/1"))
                .andExpect(status().isNotFound());

        Mockito.verify(requestService,Mockito.times(1)).deleteRequest(Mockito.anyInt());

    }

    @Test
    public void getAllRequestsFromOthers_whenValidRequest_returns200() throws Exception {

        Mockito.when(requestService.getRequestsFromOthers(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));

        mockMappingReqToReqDTO(itemRequestDTO);
        mockMvc.perform(get("/requests/all")
        .header("X-Sharer-User-Id",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDTO.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDTO.getDescription()));

        Mockito.verify(requestService,Mockito.times(1)).getRequestsFromOthers(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());
    }



    @Test
    public void getAllRequestsFromOthers_whenNoRequests_returns200WithEmptyList() throws Exception {

        Mockito.when(requestService.getRequestsFromOthers(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        mockMappingReqToReqDTO(itemRequestDTO);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id",1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", empty()));

        Mockito.verify(requestService,Mockito.times(1)).getRequestsFromOthers(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());
    }







}
