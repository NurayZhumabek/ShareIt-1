package by.nuray.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findByItemIdAndStartBeforeAndEndAfter(int itemId,
                                                        LocalDateTime end, LocalDateTime start);


    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId    " +
            "ORDER BY b.start_date DESC " +
            "LIMIT :size  " +
            "OFFSET :from", nativeQuery = true)
    List<Booking> findAllBookingsForOwner(@Param("ownerId") int ownerId,
                                          @Param("from") int from,
                                          @Param("size") int size);


    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId AND b.start_date < now() AND now() < b.end_date " +
            "ORDER BY b.start_date DESC " +
            "LIMIT :size  " +
            "OFFSET :from", nativeQuery = true)
    List<Booking> findCurrentBookingsForOwner(@Param("ownerId") int ownerId,
                                              @Param("from") int from,
                                              @Param("size") int size);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId AND b.end_date < now() " +
            "ORDER BY b.start_date DESC " +
            "LIMIT :size " +
            "OFFSET :from", nativeQuery = true)
    List<Booking> findPastBookingsForOwner(@Param("ownerId") int ownerId,
                                           @Param("from") int from,
                                           @Param("size") int size);


    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE  i.owner_id = :ownerId AND b.start_date > now() " +
            "ORDER BY  b.start_date DESC " +
            "LIMIT  :size " +
            "OFFSET  :from", nativeQuery = true)
    List<Booking> findFutureBookingsForOwner(@Param("ownerId") int ownerId,
                                             @Param("from") int from,
                                             @Param("size") int size);


    @Query(value = "SELECT  b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId AND b.status ='WAITING' " +
            "ORDER BY b.start_date DESC " +
            "LIMIT :size " +
            "OFFSET :from", nativeQuery = true)
    List<Booking> findWaitingBookingsForOwner(@Param("ownerId") int ownerId,
                                              @Param("from") int from,
                                              @Param("size") int size);


    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = :ownerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start_date DESC " +
            "LIMIT :size " +
            "OFFSET :from", nativeQuery = true)
    List<Booking> findRejectedBookingsForOwner(@Param("ownerId") int ownerId,
                                               @Param("from") int from,
                                               @Param("size") int size);


    @Query(value = "SELECT * FROM bookings  WHERE booker_id = :bookerId ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findAllBookingsForBooker(@Param("bookerId") int bookerId,
                                           @Param("from") int from,
                                           @Param("size") int size);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId AND start_date < now() AND bookings.end_date > now() ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findCurrentBookingsForBooker(@Param("bookerId") int bookerId,
                                               @Param("from") int from,
                                               @Param("size") int size);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId AND bookings.end_date < now() ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findPastBookingsForBooker(@Param("bookerId") int bookerId,
                                            @Param("from") int from,
                                            @Param("size") int size);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId AND start_date > now() ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findFutureBookingsForBooker(@Param("bookerId") int bookerId,
                                              @Param("from") int from,
                                              @Param("size") int size);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId AND status = 'WAITING' ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findWaitingBookingsForBooker(@Param("bookerId") int bookerId,
                                               @Param("from") int from,
                                               @Param("size") int size);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId AND status = 'REJECTED' ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findRejectedBookingsForBooker(@Param("bookerId") int bookerId,
                                                @Param("from") int from,
                                                @Param("size") int size);


    @Query(value = "SELECT * FROM bookings " +
            "WHERE  booker_id = :bookerId AND item_id = :itemId AND end_date < now()",
            nativeQuery = true)
    List<Booking> findAllPastBookingsForBooker(@Param("bookerId") int bookerId,
                                               @Param("itemId") int itemId);
}
