package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ItemService {

    List<ItemInfoDto> getAll(Long userId, PageRequest pageRequest);

    ItemInfoDto getById(Long itemId, Long userId);

    ItemInfoDto addItem(Long userId, ItemDto itemDto);

    ItemInfoDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long itemId);

    List<ItemInfoDto> searchItem(String text, PageRequest pageRequest);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);
}
