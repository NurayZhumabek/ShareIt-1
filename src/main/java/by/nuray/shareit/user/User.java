package by.nuray.shareit.user;

import by.nuray.shareit.booking.Booking;
import by.nuray.shareit.comment.Comment;
import by.nuray.shareit.item.Item;
import by.nuray.shareit.request.ItemRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty
    @Size(min = 2, max = 50)
    @Column(name = "username", nullable = false)
    private String username;

    @Email
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "owner")
    private List<Item> items;

    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "requester")
    private List<ItemRequest> itemRequests;


    @OneToMany(mappedBy = "author")
    private List<Comment> comments;
}
