package by.nuray.shareit.comment;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {


    private final CommentService commentService;
    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<CommentDto>> getCommentsByItem(@PathVariable int itemId) {
        List<CommentDto> comments = commentService.getCommentsByItem(itemId)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUser(@PathVariable int userId) {
        List<CommentDto> comments = commentService.getCommentsByUser(userId)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestBody @Valid CommentDto commentDto,
            BindingResult bindingResult,
            @RequestHeader("X-Sharer-User-Id") int authorId,
            @RequestHeader("X-Item-Id") int itemId) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        Comment comment = modelMapper.map(commentDto, Comment.class);
        Comment savedComment = commentService.addComment(comment, itemId, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(savedComment, CommentDto.class));
    }

}
