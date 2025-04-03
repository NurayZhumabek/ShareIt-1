package by.nuray.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ItemDto {


    @NotBlank
    private String name;
    @NotBlank
    private String description;

    private Boolean available;

    private Integer requestId;

    public ItemDto() {
    }


}
