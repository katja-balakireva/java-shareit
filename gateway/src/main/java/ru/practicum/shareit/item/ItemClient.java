package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.custom.UserIdValidationException;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        validateUserId(userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateUserId(userId);
        return patch("/" + itemId, userId, itemDto);
    }

    public void deleteItem(Long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> addComment(Long userId, CommentDto commentDto, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    /*VALIDATION METHODS*/

    private void validateUserId(Long userId) {
        if (userId < 0) throw new UserIdValidationException("id пользователя не может быть отрицательным: " + userId);
        if (userId == 0) throw new UserIdValidationException("id пользователя не может быть нулевой: " + userId);
    }
}