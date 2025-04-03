package by.nuray.shareit.comment;

import by.nuray.shareit.booking.BookingService;
import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.CommentException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


    public CommentServiceImpl(CommentRepository commentRepository, UserService userService,
                              ItemService itemService, BookingService bookingService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    @Override
    public List<Comment> getCommentsByItem(int itemId) {

        return commentRepository.findByItemId(itemId);
    }

    @Override
    public List<Comment> getCommentsByUser(int userId) {

        return commentRepository.findByAuthorId(userId);
    }

    @Override
    public Comment addComment(Comment comment, int itemId, int authorId) {
        Item item = itemService.getItemById(itemId);
        User author = userService.getUserById(authorId);

        if (comment == null) {
            throw new CommentException("Comment cannot be null");
        }
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new CommentException("Comment text cannot be empty");
        }

        boolean hasRented = bookingService.getPastBookingsByBookerForItem(authorId, itemId).size() > 0;


        if (!hasRented) {
            throw new CommentException("You cannot comment this item as you have not rented");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
