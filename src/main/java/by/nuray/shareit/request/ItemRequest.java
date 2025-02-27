package by.nuray.shareit.request;

import by.nuray.shareit.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private int id;
    private User requester;


    private String name;

    @NotEmpty
    @Size(min = 2, max = 200)
    private String description;

    private LocalDate createdDate;

    private boolean found;

}
