package by.nuray.shareit.item;



public class ItemDto {


    String name;
    String description;
    boolean available;


    public ItemDto() {
    }

    public ItemDto( String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }



    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}
