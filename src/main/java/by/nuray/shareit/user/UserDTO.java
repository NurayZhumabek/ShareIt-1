package by.nuray.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    int id;

    @NotBlank
    private String username;
    @Email
    private String email;

    public UserDTO() {
    }


}
