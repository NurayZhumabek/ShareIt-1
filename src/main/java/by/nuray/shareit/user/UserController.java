package by.nuray.shareit.user;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;


    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.ok(modelMapper.map(userService.getUserById(id), UserDTO.class));
    }


    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody UserDTO userDTO,
                                          BindingResult bindingResult) {

        User user = modelMapper.map(userDTO, User.class);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(user, UserDTO.class));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,
                                        @RequestBody @Valid UserDTO userDTO,
                                        BindingResult bindingResult) {
        User updatedUser = modelMapper.map(userDTO, User.class);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().
                    body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        userService.updateUser(id, updatedUser);

        return ResponseEntity.ok(modelMapper.map(updatedUser, UserDTO.class));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
