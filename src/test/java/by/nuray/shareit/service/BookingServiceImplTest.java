package by.nuray.shareit.service;


import by.nuray.shareit.booking.*;
import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.BookingException;
import by.nuray.shareit.util.BookingNotFoundException;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @InjectMocks
    BookingServiceImpl bookingService;

    private Booking booking;
    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {

        booker = new User();
        booker.setId(1);
        booker.setUsername("booker");

        owner = new User();
        owner.setId(2);
        owner.setUsername("owner");



        item = new Item();
        item.setName("test item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setId(1);
        item.setOwner(owner);


        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(booker);

    }

    @Test
    public void getBookingById_whenUserIsBooker_returnsBooking(){

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(booking.getId(),booker.getId());

        assertNotNull(result);


    }

    @Test
    public void getBookingById_whenUserIsOwner_returnsBooking(){

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(booking.getId(),owner.getId());

        assertNotNull(result);

    }

    @Test
    public void getBookingById_whenBookingNotFound_throwsBookingNotFoundException(){

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());

        Exception exception=assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(),owner.getId()));

        assertEquals("Booking with id 1 not found", exception.getMessage());
    }

    @Test
    public void getBookingById_whenUserIsNeitherBookerNorOwner_throwsBookingException(){
        User anotherUser = new User();
        anotherUser.setId(3);

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Exception exception=assertThrows(BookingException.class,
                () -> bookingService.getBookingById(booking.getId(),anotherUser.getId()));

        assertEquals("You do not have permission to view this booking", exception.getMessage());
    }

    @Test
    public void createBooking_whenValidInput_returnsSavedBooking() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        Mockito.when(bookingRepository.findByItemIdAndStartBeforeAndEndAfter(
                        item.getId(), booking.getEnd(), booking.getStart()))
                .thenReturn(Collections.emptyList());

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(booking, booker.getId(), item.getId());

        assertNotNull(result);
        assertEquals(booker, result.getBooker());
        assertEquals(item, result.getItem());
        assertEquals(Status.WAITING, result.getStatus());

        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    public void createBooking_whenStartTimeIsNull_throwsBookingException(){

        booking.setStart(null);
        booking.setEnd(LocalDateTime.now().plusDays(2));

        Exception exception =assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));


        assertEquals("The booking start/end time cannot be null", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void createBooking_whenEndTimeIsNull_throwsBookingException(){
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(null);

        Exception exception =assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("The booking start/end time cannot be null", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void createBooking_whenStartTimeIsInThePast_throwsBookingException(){
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        Exception exception =assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("Booking start date cannot be in the past", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void createBooking_whenEndTimeIsBeforeStartTime_throwsBookingException(){
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        Exception exception = assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("End time must be after start time", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    public void createBooking_whenOverlappingApprovedBookingExists_throwsBookingException(){
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findByItemIdAndStartBeforeAndEndAfter(item.getId(), booking.getEnd(), booking.getStart()))
                .thenReturn(Arrays.asList(booking));

        Exception exception = assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("Booking already exists and is approved", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void createBooking_whenOverlappingWaitingBookingExists_throwsBookingException(){
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.WAITING);

        Mockito.when(bookingRepository.findByItemIdAndStartBeforeAndEndAfter(item.getId(), booking.getEnd(), booking.getStart()))
                .thenReturn(Arrays.asList(booking));

        Exception exception = assertThrows(BookingException.class,
                () -> bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("Booking request for this time already exists", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void createBooking_whenBookerNotFound_throwsUserNotFoundException(){
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        Mockito.when(userService.getUserById(booker.getId()))
                .thenThrow(new UserNotFoundException("User with id " + booker.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
        ()->bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    public void createBooking_whenItemNotFound_throwsItemNotFoundException(){
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        Mockito.when(itemService.getItemById(item.getId()))
                .thenThrow(new ItemNotFoundException("Item with id " + item.getId() + " not found"));
        Exception exception = assertThrows(ItemNotFoundException.class,
                ()->bookingService.createBooking(booking, booker.getId(), item.getId()));

        assertEquals("Item with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void cancelBooking_whenBookingExistsAndUserIsBookerAndStatusIsWaiting_returnsCanceledBooking(){

        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);


        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.cancelBooking(booking.getId(), booker.getId());

        assertEquals(Status.CANCELED, result.getStatus());

    }

    @Test
    public void cancelBooking_whenBookingNotFound_throwsBookingNotFoundException(){

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());

        Exception exception= assertThrows(BookingNotFoundException.class,
                ()->bookingService.cancelBooking(booking.getId(), booker.getId()));


        assertEquals("Booking with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void cancelBooking_whenUserIsNotTheBooker_throwsBookingException(){

        User anotherUser = new User();
        anotherUser.setId(3);

        Mockito.when(bookingRepository.findById(booking.getId()))
                        .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(BookingException.class,
                ()->bookingService.cancelBooking(booking.getId(), anotherUser.getId()));

        assertEquals("You are not the booker of this booking", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void cancelBooking_whenBookingStatusIsNotWaiting_throwsBookingException	(){
        booking.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(BookingException.class,
                ()->bookingService.cancelBooking(booking.getId(), booker.getId()));

        assertEquals("Booking status is not  WAITING", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void cancelBooking_whenBookerIsNull_throwsBookingException(){

        Mockito.when(userService.getUserById(booker.getId()))
                .thenThrow(new UserNotFoundException("User with id " + booker.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                ()->bookingService.cancelBooking(booking.getId(), booker.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void updateBookingStatus_whenBookingExistsAndOwnerApproves_setsStatusToApproved() {
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);
        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.updateBookingStatus(booking.getId(), true, item.getOwner().getId());

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }


    @Test
    public void updateBookingStatus_whenBookingExistsAndOwnerRejects_setsStatusToRejected() {
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);
        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.updateBookingStatus(booking.getId(), false, item.getOwner().getId());

        assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }


    @Test
    public void updateBookingStatus_whenBookingNotFound_throwsBookingNotFoundException() {
        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBookingStatus(booking.getId(), false, item.getOwner().getId()));

        assertEquals("Booking with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }


    @Test
    public void updateBookingStatus_whenUserIsNotOwner_throwsBookingException() {
        User anotherUser = new User();
        anotherUser.setId(3);

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(BookingException.class,
                () -> bookingService.updateBookingStatus(booking.getId(), true, anotherUser.getId()));

        assertEquals("You are not the owner of this booking request", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }


    @Test
    public void updateBookingStatus_whenStatusIsAlreadyChanged_throwsBookingException() {
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);

        Mockito.when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        Exception exception = assertThrows(BookingException.class,
                () -> bookingService.updateBookingStatus(booking.getId(), false, item.getOwner().getId()));

        assertEquals("Booking status is already changed", exception.getMessage());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    public void updateBookingStatus_whenOwnerNotFound_throwsUserNotFoundException(){

        Mockito.when(userService.getUserById(booking.getItem().getOwner().getId()))
                .thenThrow(new UserNotFoundException("User with id " + booker.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                ()->bookingService.updateBookingStatus(booking.getId(),false,item.getOwner().getId()));

        assertEquals("User with id 1 not found", exception.getMessage());
        Mockito.verify(bookingRepository,Mockito.never()).save(Mockito.any(Booking.class));

    }

    @Test
    public void getPastBookingsByBookerForItem_whenValidInput_returnsPastBookings(){
        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        Mockito.when(bookingRepository.findAllPastBookingsForBooker(booker.getId(), item.getId()))
                .thenReturn(Arrays.asList(booking));


        List<Booking> result = bookingService.getPastBookingsByBookerForItem(booker.getId(), item.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));

    }

    @Test
    public void getPastBookingsByBookerForItem_whenNoPastBookings_returnsEmptyList(){

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        Mockito.when(bookingRepository.findAllPastBookingsForBooker(booker.getId(), item.getId()))
                .thenReturn(Collections.emptyList());


        List<Booking> result = bookingService.getPastBookingsByBookerForItem(booker.getId(), item.getId());

        assertNotNull(result);
        assertEquals(0, result.size());

    }

    @Test
    public void getPastBookingsByBookerForItem_whenBookerNotFound_throwsUserNotFoundException(){
        Mockito.when(userService.getUserById(booker.getId()))
                .thenThrow(new UserNotFoundException("User with id " + booker.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                ()->bookingService.getPastBookingsByBookerForItem(booker.getId(), item.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());

    }

    @Test
    public void getPastBookingsByBookerForItem_whenItemNotFound_throwsItemNotFoundException(){

        Mockito.when(itemService.getItemById(item.getId()))
                .thenThrow(new ItemNotFoundException("Item with id " + item.getId() + " not found"));

        Exception exception = assertThrows(ItemNotFoundException.class,
                ()->bookingService.getPastBookingsByBookerForItem(booker.getId(), item.getId()));

        assertEquals("Item with id 1 not found", exception.getMessage());
    }

    @Test
    public void getBookingsByBooker_whenStateIsAll_returnsAllBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findAllBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.ALL, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllBookingsForBooker(booker.getId(), from,size);

    }
    @Test
    public void getBookingsByBooker_whenStateIsCurrent_returnsCurrentBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findCurrentBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.CURRENT, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findCurrentBookingsForBooker(booker.getId(), from,size);

    }
    @Test
    public void getBookingsByBooker_whenStateIsPast_returnsPastBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findPastBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.PAST, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findPastBookingsForBooker(booker.getId(), from,size);

    }
    @Test
    public void getBookingsByBooker_whenStateIsFuture_returnsFutureBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findFutureBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.FUTURE, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findFutureBookingsForBooker(booker.getId(), from,size);

    }

    @Test
    public void getBookingsByBooker_whenStateIsWaiting_returnsWaitingBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findWaitingBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.WAITING, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findWaitingBookingsForBooker(booker.getId(), from,size);

    }

    @Test
    public void getBookingsByBooker_whenStateIsRejected_returnsRejectedBookings(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Mockito.when(bookingRepository.findRejectedBookingsForBooker(booker.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByBooker(booker.getId(), State.REJECTED, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findRejectedBookingsForBooker(booker.getId(), from,size);

    }

    @Test
    public void getBookingsByBooker_whenStateIsUnknown_throwsBookingException(){
        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(booker.getId()))
                .thenReturn(booker);

        Exception exception = assertThrows(BookingException.class,
                ()->bookingService.getBookingsByBooker(booker.getId(),State.UNKNOWN, from, size));

        assertEquals("Unknown state!", exception.getMessage());
    }

    @Test
    public void getBookingsByBooker_whenUserNotFound_throwsUserNotFoundException(){
        int from =1;
        int size=2;
        Mockito.when(userService.getUserById(booker.getId()))
                .thenThrow(new UserNotFoundException("User with id " + booker.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                ()->bookingService.getBookingsByBooker(booker.getId(),State.UNKNOWN, from, size));

        assertEquals("User with id 1 not found", exception.getMessage());

    }

    @Test
    public void getBookingsByOwner_whenStateIsAll_returnsAllBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findAllBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.ALL, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findAllBookingsForOwner(owner.getId(), from,size);
    }

    @Test
    public void getBookingsByOwner_whenStateIsCurrent_returnsCurrentBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findCurrentBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.CURRENT, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findCurrentBookingsForOwner(owner.getId(), from,size);
    }

    @Test
    public void getBookingsByOwner_whenStateIsPast_returnsPastBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findPastBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.PAST, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findPastBookingsForOwner(owner.getId(), from,size);
    }
    @Test
    public void getBookingsByOwner_whenStateIsFuture_returnsFutureBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findFutureBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.FUTURE, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findFutureBookingsForOwner(owner.getId(), from,size);
    }

    @Test
    public void getBookingsByOwner_whenStateIsWaiting_returnsWaitingBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findWaitingBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.WAITING, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findWaitingBookingsForOwner(owner.getId(), from,size);
    }
    @Test
    public void getBookingsByOwner_whenStateIsRejected_returnsRejectedBookings(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(owner);

        Mockito.when(bookingRepository.findRejectedBookingsForOwner(owner.getId(), from,size))
                .thenReturn(Arrays.asList(booking));

        List<Booking> result =bookingService.getBookingsByOwner(owner.getId(), State.REJECTED, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());

        Mockito.verify(bookingRepository, Mockito.times(1)).findRejectedBookingsForOwner(owner.getId(), from,size);
    }

    @Test
    public void getBookingsByOwner_whenStateIsUnknown_throwsBookingException(){

        int from =1;
        int size=2;

        Mockito.when(userService.getUserById(owner.getId()))
                .thenReturn(booker);

        Exception exception = assertThrows(BookingException.class,
                ()->bookingService.getBookingsByOwner(owner.getId(),State.UNKNOWN, from, size));

        assertEquals("Unknown state!", exception.getMessage());
    }

    @Test
    public void getBookingsByOwner_whenUserNotFound_throwsUserNotFoundException(){
        int from =1;
        int size=2;
        Mockito.when(userService.getUserById(owner.getId()))
                .thenThrow(new UserNotFoundException("User with id " + owner.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                ()->bookingService.getBookingsByOwner(owner.getId(),State.UNKNOWN, from, size));

        assertEquals("User with id 2 not found", exception.getMessage());

    }




























}














































