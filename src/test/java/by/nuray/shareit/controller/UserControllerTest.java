package by.nuray.shareit.controller;

import by.nuray.shareit.user.*;
import by.nuray.shareit.util.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDTO userDTO;
    private User secondUser;
    private UserDTO secondUserDTO;



    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("TestUser");
        user.setEmail("TestUser@gmail.com");

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setUsername("TestUser");
        userDTO.setEmail("TestUser@gmail.com");

         }

    private void mockMappingUserToUserDTO(UserDTO dto) {
        Mockito.when(modelMapper.map(Mockito.any(User.class), Mockito.eq(UserDTO.class)))
                .thenReturn(dto);
    }

    private void mockMappingUserDTOToUser(User user) {
        Mockito.when(modelMapper.map(Mockito.any(UserDTO.class), Mockito.eq(User.class)))
                .thenReturn(user);
    }





    @Test
    public void getUserById_whenUserExists_returnsUserDTO() throws Exception {
        Mockito.when(userService.getUserById(1)).thenReturn(user);
        mockMappingUserToUserDTO(userDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    public void getUserById_whenUserNotFound_returns404() throws Exception {
        Mockito.when(userService.getUserById(1))
                .thenThrow(new UserNotFoundException("User with id 1 not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers_whenUsersExist_returnsUserDTOList() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user));
        mockMappingUserToUserDTO(userDTO);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(userDTO.getId()))
                .andExpect(jsonPath("$[0].username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$[0].email").value(userDTO.getEmail()));

        Mockito.verify(userService).getAllUsers();
    }

    @Test
    public void getAllUsers_whenNoUsers_returnsEmptyList() throws Exception {

        Mockito.when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

    }

    @Test
    public void createUser_whenValidInputs() throws Exception {

        mockMappingUserDTOToUser(user);
        mockMappingUserToUserDTO(userDTO);

            String json= objectMapper.writeValueAsString(userDTO);


            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.username").value("TestUser"))
                    .andExpect(jsonPath("$.email").value("TestUser@gmail.com"));

            Mockito.verify(userService,Mockito.times(1)).createUser(Mockito.any(User.class));
    }

    @Test
    public void createUser_whenUsernameIsBlank_returns400() throws Exception {

        userDTO.setUsername("");

        mockMappingUserToUserDTO(userDTO);
        mockMappingUserDTOToUser(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                        .andExpect(status().isBadRequest());

        Mockito.verify(userService,Mockito.never()).createUser(Mockito.any(User.class));
    }

    @Test
    public void createUser_whenEmailIsInvalid_returns400() throws Exception {

        userDTO.setEmail("test");

        mockMappingUserToUserDTO(userDTO);
        mockMappingUserDTOToUser(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService,Mockito.never()).createUser(Mockito.any(User.class));
    }

    @Test
    public void updateUser_whenValidInput_updatesSuccessfully() throws Exception {

        userDTO.setUsername("New username");
        mockMappingUserDTOToUser(user);
        mockMappingUserToUserDTO(userDTO);

        mockMvc.perform(patch("/users/1")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("New username"))
                .andExpect(jsonPath("$.email").value("TestUser@gmail.com"));

        Mockito.verify(userService,Mockito.times(1)).updateUser(Mockito.anyInt(),Mockito.any(User.class));

    }

    @Test
    public void updateUser_whenUserNotFound_returns404() throws Exception {

        userDTO.setUsername("New username");
        mockMappingUserDTOToUser(user);


        Mockito.when(userService.updateUser(Mockito.eq(1),Mockito.any(User.class))).
                thenThrow(new UserNotFoundException("User with id 1 not found"));


        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
        Mockito.verify(userService,Mockito.times(1)).updateUser(Mockito.anyInt(),Mockito.any(User.class));
    }

    @Test
    public void updateUser_whenInvalidUsername_returns400() throws Exception {
        userDTO.setUsername("");
        mockMappingUserDTOToUser(user);
        mockMappingUserToUserDTO(userDTO);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService,Mockito.never()).updateUser(Mockito.anyInt(),Mockito.any(User.class));
    }

    @Test
    public void updateUser_whenInvalidEmail_returns400() throws Exception {
        userDTO.setEmail("test");
        mockMappingUserDTOToUser(user);
        mockMappingUserToUserDTO(userDTO);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userService,Mockito.never()).updateUser(Mockito.anyInt(),Mockito.any(User.class));

    }

    @Test
    public void deleteUser_whenValidInput_deletesSuccessfully() throws Exception {

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService,Mockito.times(1)).deleteUser(Mockito.anyInt());
    }

    @Test
    public void deleteUser_whenUserNotFound_returns404() throws Exception {

        Mockito.doThrow(new UserNotFoundException("User with id 1 not found"))
                .when(userService).deleteUser(Mockito.anyInt());

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(userService,Mockito.times(1)).deleteUser(Mockito.anyInt());

    }








}
