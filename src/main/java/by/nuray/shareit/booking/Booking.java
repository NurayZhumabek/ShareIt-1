package by.nuray.shareit.booking;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import by.nuray.shareit.util.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.Getter;

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
