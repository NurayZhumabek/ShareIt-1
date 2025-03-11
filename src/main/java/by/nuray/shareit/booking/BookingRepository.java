package by.nuray.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdAndStatus(int bookerId, State state);

    List<Booking> findByItemOwnerIdAndStatus(int ownerId, State state);

    List<Booking> findByItemIdAndStartBeforeAndEndAfter(int itemId,
                                                                 LocalDateTime end,LocalDateTime start);

    List<Booking> findByBookerIdAndItemIdAndStatus(int bookerId, int itemId, Status status);


    List<Booking> findByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            int bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            int bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            int bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            int bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            int ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            int ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            int ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            int ownerId, Status status);


}
