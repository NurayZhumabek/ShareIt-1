package by.nuray.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class ItemRequestDTO {

    private int id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private boolean found;

    public ItemRequestDTO() {
    }
}
