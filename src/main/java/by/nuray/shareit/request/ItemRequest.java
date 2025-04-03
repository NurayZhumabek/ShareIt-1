package by.nuray.shareit.request;

import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class ItemRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty
    @Size(min = 2, max = 200)
    @Column(name = "description", nullable = false)
    private String description;


    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requester;


    @OneToMany(mappedBy = "request")
    private List<Item> items;


}
