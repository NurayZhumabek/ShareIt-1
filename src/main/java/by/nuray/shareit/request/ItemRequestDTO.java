package by.nuray.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDTO {

    private int id;
    private String description;

    public ItemRequestDTO() {
    }

}
