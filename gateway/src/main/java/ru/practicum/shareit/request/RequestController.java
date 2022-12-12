package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10")
                                                 Integer size) {
        log.info("Get all requests for user id: {}", userId);
        return requestClient.getAllByUserId(userId, from, size);

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get request by id: {}, user id: {}", requestId, userId);
        return requestClient.getByRequestId(userId, requestId);

    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsNotOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                         Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                                         Integer size) {
        log.info("Get all requests by user id: {}", userId);
        return requestClient.getAllRequestsNotOwner(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add request: {}, user id: {}", itemRequestDto, userId);
        return requestClient.addItemRequest(userId, itemRequestDto);
    }
}