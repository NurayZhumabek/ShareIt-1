package by.nuray.shareit.booking;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date",nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn (name="booker_id", nullable = false)
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;


}
