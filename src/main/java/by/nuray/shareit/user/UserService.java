package by.nuray.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {


    public List<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(int id, User user);

    public void deleteUser(int id);

    public Optional<User> findByEmail(String email);
    public User getUserById(int id);


}
