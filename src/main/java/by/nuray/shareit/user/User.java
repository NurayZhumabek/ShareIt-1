package by.nuray.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {
    private int id;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String username;

    @Email
    private String email;
}
