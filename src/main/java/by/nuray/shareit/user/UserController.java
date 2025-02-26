package by.nuray.shareit.user;


import by.nuray.shareit.util.UserValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;


    public UserController(UserService userService, ModelMapper modelMapper, UserValidator userValidator) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") int id) {
        return modelMapper.map(userService.getById(id), UserDTO.class);
    }



    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {

        User user=modelMapper.map(userDTO, User.class);

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        userService.save(user);
        return new ResponseEntity(modelMapper.map(user,UserDTO.class), HttpStatus.OK);

       }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,
                                           @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        User updatedUser = modelMapper.map(userDTO, User.class);


        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        userService.update(id, updatedUser);

        return ResponseEntity.ok(modelMapper.map(updatedUser, UserDTO.class));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        userService.delete(id);
        return  ResponseEntity.ok().build();
    }


















}
