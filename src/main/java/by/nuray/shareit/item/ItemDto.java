package by.nuray.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ItemDto {


    private String name;
    private String description;
    private boolean available;


    public ItemDto() {
    }


}
