package by.nuray.shareit.item;

import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class Item {


    private int id;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String name;

    @Size(min = 2, max = 200)
    private String description;

    private boolean available;
    private User owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotEmpty @Size(min = 2, max = 50) String getName() {
        return name;
    }

    public void setName(@NotEmpty @Size(min = 2, max = 50) String name) {
        this.name = name;
    }

    public @Size(min = 2, max = 200) String getDescription() {
        return description;
    }

    public void setDescription(@Size(min = 2, max = 200) String description) {
        this.description = description;
    }

    @NotEmpty
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(@NotEmpty boolean available) {
        this.available = available;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
