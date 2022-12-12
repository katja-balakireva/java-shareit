package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        log.info("Get all items for user id {}", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item by id: {}, user id: {}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Add item: {}, user id: {}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Update item: {}, user id: {}, item id: {}", itemDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("Delete item by id: {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10")
                                             Integer size) {
        log.info("Searching items by text: {}, user id: {}", text, userId);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody CommentDto commentDto,
                                             @PathVariable Long itemId) {
        log.info("Add comment: {}, user id: {}, item id: {}", commentDto, userId, itemId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}