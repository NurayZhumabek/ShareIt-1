package by.nuray.shareit.comment;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int id;
    @NotBlank
    private String text;
    private int  authorId;
    private int itemId;
    private LocalDateTime createdAt;
}