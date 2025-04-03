package by.nuray.shareit.booking;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.BookingException;
import by.nuray.shareit.util.BookingNotFoundException;
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
        boolean isAvailable = bookingItem.getAvailable();

        if (!isAvailable) {
            throw new BookingException("Item is not available");
        }

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

        User booker = userService.getUserById(bookerId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getBooker().getId() != bookerId) {
            throw new BookingException("You are not the booker of this booking");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BookingException("Booking status is not  WAITING");
        }
        booking.setStatus(Status.CANCELED);


        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(int bookingId, boolean approved, int ownerId) {

        User owner = userService.getUserById(ownerId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new BookingException("You are not the owner of this booking request");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingException("Booking status is already changed");
        }

        Status decision = approved ? Status.APPROVED : Status.REJECTED;

        booking.setStatus(decision);
        return bookingRepository.save(booking);


    }

    @Override
    public List<Booking> getBookingsByBooker(int bookerId, State state, int from, int size) {
        userService.getUserById(bookerId);

        switch (state) {
            case ALL:
                return bookingRepository.findAllBookingsForBooker(bookerId, from, size);
            case CURRENT:
                return bookingRepository.findCurrentBookingsForBooker(bookerId, from, size);
            case PAST:
                return bookingRepository.findPastBookingsForBooker(bookerId, from, size);
            case FUTURE:
                return bookingRepository.findFutureBookingsForBooker(bookerId, from, size);
            case WAITING:
                return bookingRepository.findWaitingBookingsForBooker(bookerId, from, size);
            case REJECTED:
                return bookingRepository.findRejectedBookingsForBooker(bookerId, from, size);
            default:
                throw new BookingException("Unknown state!");
        }
    }


    @Override
    public List<Booking> getBookingsByOwner(int ownerId, State state, int from, int size) {
        userService.getUserById(ownerId);

        switch (state) {
            case ALL:
                return bookingRepository.findAllBookingsForOwner(ownerId, from, size);
            case CURRENT:
                return bookingRepository.findCurrentBookingsForOwner(ownerId, from, size);
            case PAST:
                return bookingRepository.findPastBookingsForOwner(ownerId, from, size);
            case FUTURE:
                return bookingRepository.findFutureBookingsForOwner(ownerId, from, size);
            case WAITING:
                return bookingRepository.findWaitingBookingsForOwner(ownerId, from, size);
            case REJECTED:
                return bookingRepository.findRejectedBookingsForOwner(ownerId, from, size);
            default:
                throw new BookingException("Unknown state!");
        }
    }

    @Override
    public List<Booking> getPastBookingsByBookerForItem(int bookerId, int itemId) {
        userService.getUserById(bookerId);
        itemService.getItemById(itemId);

        return bookingRepository.findAllPastBookingsForBooker(bookerId, itemId);
    }
}
