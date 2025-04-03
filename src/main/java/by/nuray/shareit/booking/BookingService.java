package by.nuray.shareit.booking;


import java.util.List;

public interface BookingService {

    public Booking createBooking(Booking booking, int bookerId, int itemId);

    public Booking getBookingById(int bookingId, int userId);

    Booking cancelBooking(int bookingId, int bookerId);

    Booking updateBookingStatus(int bookingId, boolean approved, int ownerId);

    List<Booking> getBookingsByBooker(int bookerId, State state, int from, int size);

    List<Booking> getBookingsByOwner(int ownerId, State state, int from, int size);

    List<Booking> getPastBookingsByBookerForItem(int bookerId, int itemId); // for comment class to check addComment()
}
