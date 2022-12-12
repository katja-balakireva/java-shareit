package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.custom.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("DefaultItemService")
                          ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemInfoDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                    Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10")
                                    Integer size
    ) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return itemService.getAll(userId, pageRequest);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        return itemService.getById(itemId, userId);
    }

    @PostMapping
    public ItemInfoDto addItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemInfoDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    //items/search?text={text}
    @GetMapping("/search")
    public List<ItemInfoDto> searchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                        @RequestParam String text,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                        Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10")
                                        Integer size
    ) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return itemService.searchItem(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto, @PathVariable Long itemId) {
        return itemService.addComment(commentDto, userId, itemId);
    }
}