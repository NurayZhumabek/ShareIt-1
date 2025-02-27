package by.nuray.shareit.booking;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.BookingException;
import by.nuray.shareit.util.Status;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final UserService userService;

    public BookingServiceImpl(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    Map<Integer, Booking> bookings = new HashMap<>();

    @Override
    public Booking createBooking(Booking booking) {
        if (booking.getItem().getId() == 0 || booking.getStart() == null || booking.getEnd() == null) {
            throw new BookingException("Invalid booking request.");
        }

        Item item = itemService.getById(booking.getItem().getId());
        if (item == null) {
            throw new BookingException("Item not found.");
        }

        User booker = userService.getById(booking.getBooker().getId());
        if (booker == null) {
            throw new BookingException("User not found.");
        }

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        int newId = bookings.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        booking.setId(newId);
        bookings.put(newId, booking);

        return booking;
    }


    @Override
    public Booking cancelBooking(int bookingId, int bookerId) {
        Booking booking = bookings.get(bookingId);


        if (booking == null) {
            throw new BookingException("Booking not found");
        }

        if (booking.getBooker().getId() != bookerId) {
            throw new BookingException("Booker id mismatch");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BookingException("Booking status is not waiting");
        }


        booking.setStatus(Status.CANCELED);
        bookings.put(bookingId, booking);

        return booking;

    }

    @Override
    public Booking updateBookingStatus(int bookingId, boolean updatedStatus, int ownerId) {
        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }

        if (booking.getItem() == null || booking.getItem().getOwner() == null) {
            throw new RuntimeException("Invalid request");
        }

        int owner = booking.getItem().getOwner().getId();
        if (owner != ownerId) {
            throw new BookingException("You are not owner of this item");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new RuntimeException("Status is already updated");
        }

        booking.setStatus(updatedStatus ? Status.APPROVED : Status.REJECTED);

        bookings.put(bookingId, booking);

        return booking;
    }


}
