package by.nuray.shareit.user;

import by.nuray.shareit.util.ItemNotFoundException;
import by.nuray.shareit.util.UserAlreadyExistsException;
import by.nuray.shareit.util.UserNotFoundException;
import by.nuray.shareit.util.UserValidationException;
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


        if (user.getUsername().isBlank() || user.getEmail() == null) {
            throw new UserValidationException("The username cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserValidationException("Email cannot be empty");
        }

        if (findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }

        int newId = users.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        user.setId(newId);
        users.put(user.getId(), user);
    }

    @Override
    public void update(int id, User updatedUser) {
        User currentUser = users.get(id);

        if (currentUser == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()
                && !currentUser.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {

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
        findById(id).orElseThrow(() -> new UserNotFoundException("The user with id " + id + " was not found."));

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
