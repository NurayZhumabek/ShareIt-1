package by.nuray.shareit.comment;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int id;
    private String text;
    private int  authorId;
    private int itemId;
    private LocalDateTime createdAt;
}