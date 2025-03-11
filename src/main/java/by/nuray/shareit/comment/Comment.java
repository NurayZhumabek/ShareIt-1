package by.nuray.shareit.comment;


import by.nuray.shareit.item.Item;
import by.nuray.shareit.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;


    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
