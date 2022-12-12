package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/requests")
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(@Qualifier("DefaultRequestService")
                                 RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<ItemRequestInfoDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size
    ) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return requestService.getAllByUserId(userId, pageRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return requestService.getByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequestsNotOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                           Integer from,
                                                           @Positive @RequestParam(name = "size", defaultValue = "10")
                                                           Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return requestService.getAllRequestsNotOwner(userId, pageRequest);
    }

    @PostMapping
    public ItemRequestInfoDto addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addItemRequest(userId, itemRequestDto);
    }
}