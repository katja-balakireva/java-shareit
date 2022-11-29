package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    User testAuthor;
    Item testItem;
    Comment testComment;
    CommentDto testCommentDto;

    @BeforeEach
    void setUp() {
        testAuthor = new User(1L, "AuthorName", "author@author.com");
        testItem = new Item(1L, "ItemName", "ItemDescription",
                true, testAuthor, 2L,
                new ArrayList<>());
        testComment = new Comment(1L, "CommentText", testItem.getId(), testAuthor,
                LocalDateTime.now());
        testItem.setComments(List.of(testComment));
        testCommentDto = new CommentDto(1L, "CommentText", "AuthorName", LocalDateTime.now());
    }

    @Test
    void testToCommentDto() {
        CommentDto result = commentMapper.toCommentDto(testComment);

        assertEquals(testCommentDto.getId(), result.getId());
        assertEquals(testCommentDto.getText(), result.getText());
        assertEquals(testCommentDto.getAuthorName(), result.getAuthorName());
        assertEquals(testCommentDto.getCreated(), result.getCreated());
    }

    @Test
    void testToComment() {
        Comment result = commentMapper.toComment(testCommentDto, testItem.getId(), testAuthor);

        assertEquals(testComment.getId(), result.getId());
        assertEquals(testComment.getText(), result.getText());
        assertEquals(testComment.getItemId(), result.getItemId());
        assertEquals(testComment.getAuthor().getId(), result.getAuthor().getId());
        assertEquals(testComment.getCreated(), result.getCreated());
    }
}
