package by.nuray.shareit.booking;

import java.time.LocalDate;

public interface BookingService {

    public Booking createBooking(Booking booking);

    Booking cancelBooking(int bookingId, int bookerId);

    Booking updateBookingStatus(int bookingId, boolean updatedStatus, int ownerId);
}
