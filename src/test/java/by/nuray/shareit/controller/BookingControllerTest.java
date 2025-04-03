package by.nuray.shareit.controller;


import by.nuray.shareit.booking.*;
import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.util.*;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)

@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private ItemService itemService;

    private Booking booking;
    private BookingDTO bookingDTO;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setEmail("test@test.com");

        item = new Item();
        item.setId(1);
        item.setName("test");
        item.setDescription("description");
        item.setAvailable(false);


        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));

        bookingDTO = new BookingDTO();
        bookingDTO.setId(1);
        bookingDTO.setItemId(item.getId());
        bookingDTO.setBookerId(user.getId());
        bookingDTO.setStart(LocalDateTime.now());
        bookingDTO.setEnd(LocalDateTime.now().plusDays(2));
    }

    private void mockMappingBookingToBookingDTO(BookingDTO dto) {
        Mockito.when(modelMapper.map(Mockito.any(Booking.class), Mockito.eq(BookingDTO.class)))
                .thenReturn(dto);
    }

    private void mockMappingItemDtoItem(Booking booking) {
        Mockito.when(modelMapper.map(Mockito.any(BookingDTO.class), Mockito.eq(Booking.class)))
                .thenReturn(booking);
    }

    @Test
    public void createBooking_whenValidInput_returns201() throws Exception {

        mockMappingBookingToBookingDTO(bookingDTO);
        mockMappingItemDtoItem(booking);

        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDTO.getId()));

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void createBooking_whenInvalidInput_returns400() throws Exception {

        bookingDTO.setEnd(null);
        mockMappingBookingToBookingDTO(bookingDTO);
        mockMappingItemDtoItem(booking);

        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void createBooking_whenUserNotFound_returns404() throws Exception {

        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);
        mockMappingItemDtoItem(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void createBooking_whenItemNotFound_returns404() throws Exception {

        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("Item with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);
        mockMappingItemDtoItem(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(Mockito.any(Booking.class), Mockito.anyInt(), Mockito.anyInt());

    }


    @Test
    public void cancelBooking_whenValidBooker_returnsCancelledBooking() throws Exception {
        booking.setStatus(Status.CANCELED);

        Mockito.when(bookingService.cancelBooking(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);

        mockMappingBookingToBookingDTO(bookingDTO);
        bookingDTO.setStatus(Status.CANCELED);

        mockMvc.perform(patch("/bookings/cancel/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDTO.getId()))
                .andExpect(jsonPath("$.status").value(bookingDTO.getStatus().toString()));

        Mockito.verify(bookingService, Mockito.times(1)).cancelBooking(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void cancelBooking_whenInvalidBooker_returns400() throws Exception {


        Mockito.when(bookingService.cancelBooking(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new BookingException("You are not the booker of this booking"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(patch("/bookings/cancel/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        Mockito.verify(bookingService, Mockito.times(1)).cancelBooking(Mockito.anyInt(), Mockito.anyInt());


    }

    @Test
    public void cancelBooking_whenBookingNotFound_returns404() throws Exception {

        Mockito.when(bookingService.cancelBooking(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new BookingNotFoundException("Booking with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(patch("/bookings/cancel/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).cancelBooking(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void updateBooking_whenOwnerApproves_returnsApproved() throws Exception {
        booking.setStatus(Status.APPROVED);

        int id = 1;
        int ownerId = 1;
        boolean approved = true;

        Mockito.when(bookingService.updateBookingStatus(id, approved, ownerId))
                .thenReturn(booking);

        mockMappingBookingToBookingDTO(bookingDTO);
        bookingDTO.setStatus(Status.APPROVED);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("updatedStatus", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDTO.getId()))
                .andExpect(jsonPath("$.status").value(bookingDTO.getStatus().toString()));
        Mockito.verify(bookingService, Mockito.times(1)).updateBookingStatus(id, approved, ownerId);

    }

    @Test
    public void updateBooking_whenOwnerRejects_returnsRejected() throws Exception {
        booking.setStatus(Status.REJECTED);
        int id = 1;
        int ownerId = 1;
        boolean approved = false;
        Mockito.when(bookingService.updateBookingStatus(id, approved, ownerId))
                .thenReturn(booking);

        mockMappingBookingToBookingDTO(bookingDTO);
        bookingDTO.setStatus(Status.REJECTED);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("updatedStatus", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDTO.getId()))
                .andExpect(jsonPath("$.status").value(bookingDTO.getStatus().toString()));


        Mockito.verify(bookingService, Mockito.times(1)).updateBookingStatus(id, approved, ownerId);

    }

    @Test
    public void updateBooking_whenStatusAlreadyChanged_returns400() throws Exception {

        int id = 1;
        int ownerId = 1;
        boolean approved = false;
        Mockito.when(bookingService.updateBookingStatus(id, approved, ownerId))
                .thenThrow(new BookingException("Booking status is already changed"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("updatedStatus", String.valueOf(approved)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1)).updateBookingStatus(id, approved, ownerId);

    }

    @Test
    public void updateBooking_whenNotOwner_returns400() throws Exception {

        int id = 1;
        int ownerId = 1;
        boolean approved = false;
        Mockito.when(bookingService.updateBookingStatus(id, approved, ownerId))
                .thenThrow(new BookingException("You are not the owner of this booking request"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("updatedStatus", String.valueOf(approved)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1)).updateBookingStatus(id, approved, ownerId);

    }

    @Test
    public void updateBooking_whenBookingNotFound_returns404() throws Exception {
        int id = 1;
        int ownerId = 1;
        boolean approved = false;
        Mockito.when(bookingService.updateBookingStatus(id, approved, ownerId))
                .thenThrow(new BookingNotFoundException("Booking with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("updatedStatus", String.valueOf(approved)))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).updateBookingStatus(id, approved, ownerId);

    }

    @Test
    public void getBookingsByBooker_whenValidUserAndState_returnsList() throws Exception {

        int bookerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(bookerId, State.ALL, from, size))
                .thenReturn(List.of(booking));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(bookerId, State.ALL, from, size);

    }

    @Test
    public void getBookingsByBooker_whenInvalidState_returns400() throws Exception {
        int bookerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(bookerId, State.UNKNOWN, from, size))
                .thenThrow(new BookingException("Unknown state!"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", State.UNKNOWN.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(bookerId, State.UNKNOWN, from, size);
    }

    @Test
    public void getBookingsByBooker_whenUserNotFound_returns404() throws Exception {

        int bookerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(bookerId, State.ALL, from, size))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(bookerId, State.ALL, from, size);

    }


    @Test
    public void getBookingsByOwner_whenValidUserAndState_returnsList() throws Exception {

        int ownerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(ownerId, State.ALL, from, size))
                .thenReturn(List.of(booking));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(ownerId, State.ALL, from, size);

    }

    @Test
    public void getBookingsByOwner_whenInvalidState_returns400() throws Exception {
        int ownerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(ownerId, State.UNKNOWN, from, size))
                .thenThrow(new BookingException("Unknown state!"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", State.UNKNOWN.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(ownerId, State.UNKNOWN, from, size);
    }

    @Test
    public void getBookingsByOwner_whenUserNotFound_returns404() throws Exception {

        int ownerId = 1;
        int from = 1;
        int size = 2;

        Mockito.when(bookingService.getBookingsByBooker(ownerId, State.ALL, from, size))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(ownerId, State.ALL, from, size);

    }

    @Test
    public void getBookingById_whenUserIsOwnerOrBooker_returnsBooking() throws Exception {


        Mockito.when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(booking);

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingById(booking.getId(), 1);

    }

    @Test
    public void getBookingById_whenBookingNotFound_returns404() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new BookingNotFoundException("Booking with id 1 not found"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingById(booking.getId(), 1);
    }

    @Test
    public void getBookingById_whenUserIsNotAllowed_returns400() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new BookingException("You do not have permission to view this booking"));

        mockMappingBookingToBookingDTO(bookingDTO);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.times(1)).getBookingById(booking.getId(), 1);
    }


}
