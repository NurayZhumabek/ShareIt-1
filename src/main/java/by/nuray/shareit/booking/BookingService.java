package by.nuray.shareit.booking;


import by.nuray.shareit.util.State;

import java.util.List;

public interface BookingService {

    public Booking createBooking(Booking booking, int bookerId, int itemId);

    public Booking getBookingById(int bookingId, int userId);

    Booking cancelBooking(int bookingId, int bookerId);

    Booking updateBookingStatus(int bookingId, boolean approved, int ownerId);

    List<Booking> getBookingsByBooker(int bookerId, State state);

    List<Booking> getBookingsByOwner(int ownerId, State state);
}
