package by.nuray.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {


    public Optional<User> findById(int id);

    public User getById(int id);

    public List<User> getAll();

    public void save(User user);

    public void update(int id, User user);

    public void delete(int id);

    public Optional<User> findByEmail(String email);

}
