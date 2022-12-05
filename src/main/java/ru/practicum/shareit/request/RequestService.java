package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface RequestService {

    ItemRequestInfoDto getByRequestId(Long userId, Long requestId);

    List<ItemRequestInfoDto> getAllByUserId(Long userId, PageRequest pageRequest);

    ItemRequestInfoDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> getAllRequestsNotOwner(Long userId, PageRequest pageRequest);
}
