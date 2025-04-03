package by.nuray.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDTO {


    private int id;
    @NotBlank
    private String description;

    public ItemRequestDTO() {
    }

}
