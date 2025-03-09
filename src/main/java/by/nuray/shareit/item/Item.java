package by.nuray.shareit.item;

import by.nuray.shareit.booking.Booking;
import by.nuray.shareit.comment.Comment;
import by.nuray.shareit.request.ItemRequest;
import by.nuray.shareit.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "items")
@Data
public class Item {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty
    @Size(min = 2, max = 50)
    @Column(name = "name",nullable = false)
    private String name;

    @Size(min = 2, max = 200)
    @Column(name = "description")
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;


    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = true)
    private ItemRequest request;


    @OneToMany(mappedBy = "item")
    private List<Booking> bookingList;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments;







}
