package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.custom.PaginationException;
import ru.practicum.shareit.custom.UserIdValidationException;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllByUserId(Long userId, Integer from, Integer size) {
        validatePaginationParameters(from);
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, params);

    }

    public ResponseEntity<Object> getByRequestId(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllRequestsNotOwner(Long userId, Integer from, Integer size) {
        validatePaginationParameters(from);
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        validateUserId(userId);
        return post("", userId, itemRequestDto);
    }

    /*VALIDATION METHODS*/

    private void validateUserId(Long userId) {
        if (userId < 0) throw new UserIdValidationException("id пользователя не может быть отрицательным: " + userId);
        if (userId == 0) throw new UserIdValidationException("id пользователя не может быть нулевой: " + userId);
    }

    private void validatePaginationParameters(Integer param) {
        if (param < 0) throw new PaginationException("Передан неверный параметр пагинации: " + param);
    }
}