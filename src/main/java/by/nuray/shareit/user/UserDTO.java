package by.nuray.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    int id;
    private String username;
    private String email;

    public UserDTO() {
    }




}
