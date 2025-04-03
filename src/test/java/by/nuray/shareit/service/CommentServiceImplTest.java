package by.nuray.shareit.service;


import by.nuray.shareit.booking.Booking;
import by.nuray.shareit.booking.BookingService;
import by.nuray.shareit.comment.Comment;
import by.nuray.shareit.comment.CommentRepository;
import by.nuray.shareit.comment.CommentServiceImpl;
import by.nuray.shareit.item.Item;
import by.nuray.shareit.item.ItemService;
import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.CommentException;
import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    BookingService bookingService;

    private Comment comment;
    private User commentator;
    private User itemOwner;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        commentator = new User();
        commentator.setId(1);
        commentator.setUsername("commentator");

        itemOwner = new User();
        itemOwner.setId(2);
        itemOwner.setUsername("owner");

        item = new Item();
        item.setId(1);
        item.setOwner(itemOwner);

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);


        comment = new Comment();
        comment.setText("This is a comment");
        comment.setAuthor(commentator);

    }

    @Test
    public void getCommentsByItem_whenCommentsExist_returnsCommentList() {

        Mockito.when(commentRepository.findByItemId(item.getId()))
                .thenReturn(Arrays.asList(comment));

        List<Comment> comments = commentService.getCommentsByItem(item.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());


    }

    @Test
    public void getCommentsByItem_whenNoComments_returnsEmptyList() {
        Mockito.when(commentRepository.findByItemId(item.getId()))
                .thenReturn(Collections.emptyList());

        List<Comment> comments = commentService.getCommentsByItem(item.getId());

        Assertions.assertTrue(comments.isEmpty());

    }

    @Test
    public void getCommentsByItem_whenItemHasMultipleComments_returnsAllComments(){

        Comment secondComment = new Comment();
        secondComment.setText("This is a second comment");
        Mockito.when(commentRepository.findByItemId(item.getId()))
                .thenReturn(Arrays.asList(comment,secondComment));

        List<Comment> comments = commentService.getCommentsByItem(item.getId());

        assertNotNull(comments);
        assertEquals(2, comments.size());

    }

    @Test
    public void getCommentsByUser_whenUserHasComments_returnsCommentList(){
        Mockito.when(commentRepository.findByAuthorId(commentator.getId()))
                .thenReturn(Arrays.asList(comment));

        List<Comment> comments = commentService.getCommentsByUser(commentator.getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
    }

    @Test
    public void getCommentsByUser_whenUserHasNoComments_returnsEmptyList() {

        Mockito.when(commentRepository.findByAuthorId(commentator.getId()))
                .thenReturn(Collections.emptyList());

        List<Comment> comments = commentService.getCommentsByUser(commentator.getId());

        Assertions.assertTrue(comments.isEmpty());
    }

    @Test
    public void getCommentsByUser_whenUserHasMultipleComments_returnsAllComments(){
        Comment secondComment = new Comment();
        secondComment.setText("This is a second comment");
        comment.setAuthor(commentator);

        Mockito.when(commentRepository.findByAuthorId(commentator.getId()))
                .thenReturn(Arrays.asList(comment,secondComment));

        List<Comment> comments = commentService.getCommentsByUser(commentator.getId());

        assertNotNull(comments);
        assertEquals(2, comments.size());

    }

    @Test
    public void addComment_whenValidInput_returnsSavedComment(){

        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        Mockito.when(itemService.getItemById(item.getId()))
                .thenReturn(item);

        Mockito.when(userService.getUserById(commentator.getId()))
                .thenReturn(commentator);

        Mockito.when(bookingService.getPastBookingsByBookerForItem(commentator.getId(), item.getId()))
                .thenReturn(Arrays.asList(booking));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result  = commentService.addComment(comment,item.getId(),commentator.getId());

        assertNotNull(result);
        assertEquals(item, result.getItem());
        assertEquals(commentator, result.getAuthor());
        assertNotNull(result.getCreatedAt());
        assertEquals(comment.getText(), result.getText());


        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));

    }

    @Test
    public void addComment_whenCommentIsNull_throwsCommentException(){

        comment = null;

        Exception exception = assertThrows(CommentException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("Comment cannot be null", exception.getMessage());
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any(Comment.class));

    }

    @Test
    public void addComment_whenTextIsNull_throwsCommentException(){
        comment.setText(null);

        Exception exception = assertThrows(CommentException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("Comment text cannot be empty", exception.getMessage());
    }

    @Test
    public void addComment_whenTextIsBlank_throwsCommentException(){
        comment.setText(" ");
        Exception exception = assertThrows(CommentException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("Comment text cannot be empty", exception.getMessage());
    }

    @Test
    public void addComment_whenUserHasNoPastBooking_throwsCommentException(){

        Mockito.when(bookingService.getPastBookingsByBookerForItem(commentator.getId(), item.getId()))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(CommentException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("You cannot comment this item as you have not rented", exception.getMessage());

    }

    @Test
    public void addComment_whenAuthorNotFound_throwsUserNotFoundException(){
        Mockito.when(userService.getUserById(commentator.getId()))
                .thenThrow(new UserNotFoundException("User with id " + commentator.getId() + " not found"));

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("User with id 1 not found", exception.getMessage());

    }

    @Test
    public void addComment_whenItemNotFound_throwsItemNotFoundException(){
        Mockito.when(itemService.getItemById(item.getId()))
                .thenThrow(new ItemNotFoundException("Item with id " + item.getId() + " not found"));

        Exception exception = assertThrows(ItemNotFoundException.class,
                () -> commentService.addComment(comment,item.getId(),commentator.getId()));

        assertEquals("Item with id 1 not found", exception.getMessage());
    }










}
