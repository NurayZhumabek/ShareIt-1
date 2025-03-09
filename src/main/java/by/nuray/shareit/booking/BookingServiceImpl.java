package by.nuray.shareit.booking;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.BookingException;
import by.nuray.shareit.util.BookingNotFoundException;
import by.nuray.shareit.util.State;
import by.nuray.shareit.util.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public Booking createBooking(Booking booking, int bookerId, int itemId) {

        User booker = userService.getUserById(bookerId);
        Item bookingItem = itemService.getItemById(itemId);

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BookingException("The booking start/end time cannot be null");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Booking start date cannot be in the past");
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new BookingException("End time must be after start time");
        }

        List<Booking> overlappingBookings = bookingRepository.findByItemIdAndStartBeforeAndEndAfter(
                itemId, booking.getEnd(), booking.getStart());

        boolean hasApproved = overlappingBookings.stream()
                .anyMatch(b -> b.getStatus() == Status.APPROVED);

        boolean hasWaiting = overlappingBookings.stream()
                .anyMatch(b -> b.getStatus() == Status.WAITING);

        if (hasApproved) {
            throw new BookingException("Booking already exists and is approved");
        }

        if (hasWaiting) {
            throw new BookingException("Booking request for this time already exists");
        }

        booking.setBooker(booker);
        booking.setItem(bookingItem);
        booking.setStatus(Status.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new BookingException("You do not have permission to view this booking");
        }

        return booking;
    }

    @Override
    public Booking cancelBooking(int bookingId, int bookerId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));


        if (booking.getBooker() == null || booking.getBooker().getId() != bookerId) {
            throw new BookingException("You are not the booker of this booking");
        }

        if (booking.getStatus() != Status.WAITING){
            throw new BookingException("Booking status is not  WAITING");
        }
        booking.setStatus(Status.CANCELED);


        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(int bookingId, boolean approved, int ownerId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getItem().getOwner() == null ||booking.getItem().getOwner().getId() != ownerId) {
            throw new BookingException("You are not the owner of this booking request");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingException("Booking status is already changed");
        }

        Status decision = approved ? Status.APPROVED:Status.REJECTED;

        booking.setStatus(decision);
        return bookingRepository.save(booking);


    }

    @Override
    public List<Booking> getBookingsByBooker(int bookerId, State state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, now, now);
            case PAST:
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId, now);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId, now);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        bookerId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        bookerId, Status.REJECTED);
            default:
                throw new BookingException("Invalid booking state: " + state);
        }
    }

    @Override
    public List<Booking> getBookingsByOwner(int ownerId, State state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, now, now);
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        ownerId, now);
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, now);
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, Status.REJECTED);
            default:
                throw new BookingException("Invalid booking state: " + state);
        }
    }

}
