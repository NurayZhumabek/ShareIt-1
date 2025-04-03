package by.nuray.shareit.service;


import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserRepository;
import by.nuray.shareit.user.UserServiceImpl;
import by.nuray.shareit.util.UserAlreadyExistsException;
import by.nuray.shareit.util.UserNotFoundException;
import by.nuray.shareit.util.UserValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;


    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("Test User");
        user.setEmail("test@test.com");
    }

    @Test
    public void getAllUsers_WhenUsersExist_ReturnsUserList() {

        Mockito.when(userRepository.findAll())
                .thenReturn(Arrays.asList(user));

        List<User> users = userService.getAllUsers();

        Assertions.assertNotNull(users);
        assertEquals(1, users.size());

    }

    @Test
    public void getAllUsers_WhenNoUsers_ReturnsEmptyList() {

        Mockito.when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();

        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    public void getUserById_WhenUserExists_ReturnsUser() {

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        User user = userService.getUserById(1);

        Assertions.assertNotNull(user);

    }


    @Test
    public void getUserById_WhenUserNotFound_ThrowsException() {

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1));

    }

    @Test
    public void whenCreateUser_ReturnsUser() {

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        User user1 = userService.createUser(user);

        Assertions.assertNotNull(user1);
        assertEquals("Test User", user1.getUsername());
        assertEquals("test@test.com", user1.getEmail());

    }

    @Test
    public void createUser_whenUserIsNull_ThrowsException() {

        Assertions.assertThrows(UserValidationException.class,
                () -> userService.createUser(null));
    }



    @Test
    public void createUser_whenUsernameIsNull_ThrowsException() {

        user.setUsername(null);

        Assertions.assertThrows(UserValidationException.class,
                () -> userService.createUser(user));
    }

    @Test
    public void createUser_whenUsernameIsBlank_ThrowsException() {
        user.setUsername(" ");

        Assertions.assertThrows(UserValidationException.class,
                () -> userService.createUser(user));
    }

    @Test
    public void createUser_whenUserExists_ThrowsException() {
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));


        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(user));
    }

    @Test
    public void createUser_whenEmailIsNull_ThrowsException() {

        user.setEmail(null);

        Assertions.assertThrows(UserValidationException.class,
                () -> userService.createUser(user));
    }

    @Test
    public void createUser_whenEmailIsBlank_ThrowsException() {
        user.setEmail(" ");

        Assertions.assertThrows(UserValidationException.class,
                () -> userService.createUser(user));
    }


    @Test
    public void updateUser_whenValidInput_returnsUpdatedUser() {

        user.setUsername("UpdatedTest User");
        user.setEmail("UpdatedTest@test.com");

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                        .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        User updatedUser = userService.updateUser(user.getId(), user);

        Assertions.assertNotNull(updatedUser);
        assertEquals("UpdatedTest User", user.getUsername());
        assertEquals("UpdatedTest@test.com", user.getEmail());
        Mockito.verify(userRepository).save(Mockito.any(User.class));

    }

    @Test
    public void updateUser_whenIsNotExists_ThrowsException() {

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());


       Exception exception =  Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1, user));

       assertEquals("User with id 1 not found", exception.getMessage());

       Mockito.verify(userRepository,Mockito.never()).save(Mockito.any(User.class));
    }


    @Test
    public void updateUser_whenUsernameIsNull_doesNotUpdateUsername() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("Test User");
        existingUser.setEmail("test@test.com");

        User updateData = new User();
        updateData.setEmail("UpdatedTest@test.com");
        updateData.setUsername(null);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(1, updateData);

        assertEquals("Test User", existingUser.getUsername()); //
        assertEquals("UpdatedTest@test.com", existingUser.getEmail());
    }


    @Test
    public void updateUser_whenUsernameIsBlank_doesNotUpdateUsername() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("Test User");
        existingUser.setEmail("test@test.com");

        User updateData = new User();
        updateData.setEmail("UpdatedTest@test.com");
        updateData.setUsername(" ");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(1, updateData);

        assertEquals("Test User", existingUser.getUsername());
        assertEquals("UpdatedTest@test.com", existingUser.getEmail());

    }

    @Test
    public void updateUser_whenEmailIsNull_doesNotUpdateEmail() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("Test User");
        existingUser.setEmail("test@test.com");

        User updateData = new User();
        updateData.setEmail(null);
        updateData.setUsername("UpdatedTest");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(1, updateData);

        assertEquals("UpdatedTest", existingUser.getUsername()); //
        assertEquals("test@test.com", existingUser.getEmail());
    }


    @Test
    public void updateUser_whenEmailIsBlank_doesNotUpdateEmail() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("Test User");
        existingUser.setEmail("test@test.com");

        User updateData = new User();
        updateData.setEmail(" ");
        updateData.setUsername("UpdatedTest");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(1, updateData);

        assertEquals("UpdatedTest", existingUser.getUsername());
        assertEquals("test@test.com", existingUser.getEmail());

    }


    @Test
    public void updateUser_whenEmailAlreadyExists_throwsException() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("Test User");
        existingUser.setEmail("test@test.com");

        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setUsername("Existing User");
        anotherUser.setEmail("existing@test.com");

        User updateData = new User();
        updateData.setUsername("UpdatedTest");
        updateData.setEmail("existing@test.com");

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByEmail("existing@test.com"))
                .thenReturn(Optional.of(anotherUser));

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateUser(1, updateData));
    }


    @Test
    public void deleteUser_whenUserIsNotExists_throwsException() {

        Mockito.when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(Boolean.FALSE);


        Assertions.assertThrows(UserNotFoundException.class,
                ()->userService.deleteUser(1));
    }

    @Test
    public void deleteUser_DeleteSuccess() {
        Mockito.when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(Boolean.TRUE);

        userService.deleteUser(1);

    Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyInt());
    }








}







