package by.nuray.shareit.booking;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import by.nuray.shareit.util.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private int id;

    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
