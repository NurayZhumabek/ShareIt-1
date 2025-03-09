package by.nuray.shareit.comment;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentsByItem(int itemId);

    List<Comment> getCommentsByUser(int userId);


    Comment addComment(Comment comment, int itemId, int authorId);
}
