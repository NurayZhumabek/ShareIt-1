package by.nuray.shareit.controller;

import by.nuray.shareit.comment.Comment;
import by.nuray.shareit.comment.CommentController;
import by.nuray.shareit.comment.CommentDto;
import by.nuray.shareit.comment.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;


    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1);
        comment.setText("Nice item");

        commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Nice item");
    }

    private void mockMappingItemToItemDTO(CommentDto dto) {
        Mockito.when(modelMapper.map(Mockito.any(Comment.class), Mockito.eq(CommentDto.class)))
                .thenReturn(dto);
    }

    private void mockMappingItemDtoItem(Comment comment) {
        Mockito.when(modelMapper.map(Mockito.any(CommentDto.class), Mockito.eq(Comment.class)))
                .thenReturn(comment);
    }

    @Test
    public void addComment_whenValidInput_returns201() throws Exception {

        Mockito.when(commentService.addComment(Mockito.any(Comment.class),
                        Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(comment);

        mockMappingItemDtoItem(comment);
        mockMappingItemToItemDTO(commentDto);

        mockMvc.perform(post("/comments")
                        .header("X-Sharer-User-Id", 1)
                        .header("X-Item-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()));

        Mockito.verify(commentService, Mockito.times(1))
                .addComment(Mockito.any(Comment.class), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void addComment_whenMissingHeader_returns400() throws Exception {

        mockMvc.perform(post("/comments")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(commentService, Mockito.never())
                .addComment(Mockito.any(Comment.class), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void addComment_whenInvalidInput_returns400() throws Exception {

        commentDto.setText("");

        mockMappingItemDtoItem(comment);

        mockMvc.perform(post("/comments")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(commentService, Mockito.never())
                .addComment(Mockito.any(Comment.class), Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    public void getCommentsByItem_whenCommentsExist_returns200WithList() throws Exception {

        Mockito.when(commentService.getCommentsByItem(Mockito.anyInt()))
                .thenReturn(List.of(comment));

        mockMappingItemToItemDTO(commentDto);

        mockMvc.perform(get("/comments/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].text").value(commentDto.getText()));

        Mockito.verify(commentService, Mockito.times(1))
                .getCommentsByItem(Mockito.anyInt());
    }


    @Test
    public void getCommentsByItem_whenNoComments_returns200WithEmptyList() throws Exception {

        Mockito.when(commentService.getCommentsByItem(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        mockMappingItemToItemDTO(commentDto);

        mockMvc.perform(get("/comments/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.empty()));

        Mockito.verify(commentService, Mockito.times(1))
                .getCommentsByItem(Mockito.anyInt());
    }

    @Test
    public void getCommentsByUser_whenCommentsExist_returns200WithList() throws Exception {
        Mockito.when(commentService.getCommentsByUser(Mockito.anyInt()))
                .thenReturn(List.of(comment));

        mockMappingItemToItemDTO(commentDto);

        mockMvc.perform(get("/comments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].text").value(commentDto.getText()));

        Mockito.verify(commentService, Mockito.times(1))
                .getCommentsByUser(Mockito.anyInt());
    }

    @Test
    public void getCommentsByUser_whenNoComments_returns200WithEmptyList() throws Exception {
        Mockito.when(commentService.getCommentsByUser(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        mockMappingItemToItemDTO(commentDto);

        mockMvc.perform(get("/comments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.empty()));

        Mockito.verify(commentService, Mockito.times(1))
                .getCommentsByUser(Mockito.anyInt());
    }


}
