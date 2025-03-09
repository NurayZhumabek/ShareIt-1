package by.nuray.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(int itemId);
    List<Comment> findByAuthorId(int userId);

}
