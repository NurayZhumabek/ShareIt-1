package by.nuray.shareit.user;

import by.nuray.shareit.util.UserAlreadyExistsException;
import by.nuray.shareit.util.UserNotFoundException;
import by.nuray.shareit.util.UserValidationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {

        if (user == null) {
            throw new UserValidationException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new UserValidationException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserValidationException("Email is required");
        }
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with " + user.getEmail() + " already exists");
        }
        return userRepository.save(user);

    }

    @Override
    public User updateUser(int id, User user) {

        User currentUser = getUserById(id);

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            currentUser.setUsername(user.getUsername());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()
                && !user.getEmail().equals(currentUser.getEmail())) {

            if (findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("This email already exists");
            }
            currentUser.setEmail(user.getEmail());
        }
        return userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }
}
