package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
@NoArgsConstructor
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentDto commentDto, Long itemId, User author) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .itemId(itemId)
                .author(author)
                .created(commentDto.getCreated())
                .build();
    }
}
