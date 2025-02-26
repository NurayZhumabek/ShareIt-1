package by.nuray.shareit.user;

import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.UserAlreadyExistsException;
import by.nuray.shareit.util.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserServiceImpl implements UserService {

    Map<Integer, User> users = new HashMap<>();


    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User getById(int id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException("The user with id " + id + " was not found."));

    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void save(User user) {
        int newId = users.keySet().stream().max(Integer::compareTo).orElse(0) + 1;

        user.setId(newId);
        users.put(newId, user);
    }

    @Override
    public void update(int id, User updatedUser) {
        User currentUser = users.get(id);;

        if (currentUser == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }



        if (updatedUser.getEmail() != null && !currentUser.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
            if (findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("User with email " + updatedUser.getEmail() + " already exists.");
            }
            currentUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank()) {
            currentUser.setUsername(updatedUser.getUsername());
        }



    users.put(id, currentUser);
    }




    @Override
    public void delete(int id) {
        findById(id).orElseThrow(()->new UserNotFoundException("The user with id " + id + " was not found."));

        users.remove(id);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}
