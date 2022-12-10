package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.custom.RequestNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultRequestService")
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public RequestServiceImpl(ItemRequestRepository itemRequestRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              ItemMapper itemMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }


    public ItemRequestInfoDto getByRequestId(Long userId, Long requestId) {
        validateUserId(userId);
        validateAndReturnUser(userId);
        ItemRequest itemRequest = validateAndReturnRequest(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        List<ItemDto> resultItems = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        ItemRequestInfoDto result = ItemRequestMapper.toItemRequestInfoDto(itemRequest, resultItems);
        return result;
    }

    public List<ItemRequestInfoDto> getAllByUserId(Long userId, PageRequest pageRequest) {
        validateAndReturnUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserId(userId, pageRequest);

        return itemRequests.stream()
                .map(r -> ItemRequestMapper.toItemRequestInfoDto(
                        r,
                        itemRepository.findAllByRequestId(r.getId()).stream()
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        // return null;
    }

    public ItemRequestInfoDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        validateUserId(userId);
        User user = validateAndReturnUser(userId);
        ItemRequest requestToAdd = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequestRepository.save(requestToAdd);
        ItemRequestInfoDto result = ItemRequestMapper.toItemRequestInfoDto(requestToAdd, new ArrayList<>());
        return result;
    }

    public List<ItemRequestInfoDto> getAllRequestsNotOwner(Long userId, PageRequest pageRequest) {
        validateUserId(userId);
        validateAndReturnUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllOthersByUserId(userId, pageRequest);
        return itemRequests.stream()
                .map(r -> ItemRequestMapper.toItemRequestInfoDto(
                        r, itemRepository.findAllByRequestId(r.getId()).stream()
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /* VALIDATION METHODS */

    private User validateAndReturnUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            return user.get();
        }
    }

    private ItemRequest validateAndReturnRequest(Long requestId) {
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);
        if (request.isEmpty()) {
            log.warn("Запрос с id {} не найден", requestId);
            throw new RequestNotFoundException("Запрос не найден");
        } else {
            return request.get();
        }
    }

    private void validateUserId(Long userId) {
        if (userId <= 0) {
            log.warn("id пользователя не может быть отрицательным или нулевым: {}", userId);
            throw new UserNotFoundException("Некорректный id пользователя");
        }
    }
}
