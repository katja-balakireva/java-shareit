package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidateOwnershipException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultItemService")
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
    }

    // +
    public List<ItemInfoDto> getAll(Long userId) {
        validateUser(userId);

        List<ItemInfoDto> result =
                itemRepository.findByOwnerId(userId).stream()
                        .map(itemMapper::toItemInfoDto) // maybe add sorting
                        .collect(Collectors.toList());
        log.info("Получен список из {} вещей: {}", result.size(), result);

//        if (userId == null) {
//            result = itemRepository.getAll().stream()
//                    .map(ItemMapper::toItemDto)
//                    .collect(Collectors.toList());
//            log.info("Получен список из {} вещей: {}", result.size(), result);
//        } else {
//            result = itemRepository.getAllByUser(userId).stream()
//                    .map(ItemMapper::toItemDto)
//                    .collect(Collectors.toList());
//            log.info("Получен список из {} вещей пользователя {}: {}", result.size(), userId, result);
//        }
        return result;
    }

    // + //может быть убрать юзер айди
    public ItemInfoDto getById(Long itemId, Long userId) {
        validateUserIdIsNull(userId);
        validateUser(userId);

        Item item = validateAndReturnItem(itemId);
        ItemInfoDto result = itemMapper.toItemInfoDto(item);

        log.info("Получена вещь с id {}: {}", itemId, result);
        return result;
    }

    // +
    public ItemInfoDto addItem(Long userId, ItemDto itemDto) {
        validateUser(userId);
        Item item = itemMapper.toItem(itemDto, userId);
        Item itemToAdd = itemRepository.save(item);
        ItemInfoDto result = itemMapper.toItemInfoDto(itemToAdd);
        log.info("Пользователь {} добавил новую вещь: {}", userId, result);
        return result;
    }

    // +
    public ItemInfoDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateUserIdIsNull(userId);
        validateUser(userId);

        itemDto.setId(itemId);

        Item itemInDB = validateAndReturnItem(itemId);
        validateOwner(itemInDB.getOwner().getId(),userId);

        if (itemDto.getName() != null) {
            itemInDB.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemInDB.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemInDB.setAvailable(itemDto.getAvailable());
        }

        Item itemToUpdate = itemRepository.save(itemInDB);
        ItemInfoDto result = itemMapper.toItemInfoDto(itemToUpdate);
        log.info("Пользователь {} обновил вещь с id {}: {}", userId, itemId, result);
        return result;
    }

    public void deleteItem(Long itemId) {
        Item item = validateAndReturnItem(itemId);
        log.info("Вещь с id {} удалена", itemId);
        itemRepository.delete(item);
    }

    //+
    public List<ItemInfoDto> searchItem(String text) {

        List<ItemInfoDto> result;
        if (text.isBlank() || text.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(text, text).stream()
                    .filter(Item::getAvailable)
                    .map(itemMapper::toItemInfoDto)
                    .collect(Collectors.toList()); // check availability here
            log.info("Найденные вещи {}: ", result);
        }

        return result;
    }

    /* COMMENTS METHODS */

    /* VALIDATION METHODS */

    private void validateOwner(Long ownerId, Long userId) {
        if (!userId.equals(ownerId)) {
            log.warn("Пользователь с id {} не является владельцем вещи. id владельца: {}", userId, ownerId);
            throw new ValidateOwnershipException("Обновлять вещь может только владелец");
        }
    }

    private Item validateAndReturnItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ItemNotFoundException("Вещь не найдена");
        } else {
            return item.get();
        }
    }

    private void validateUserIdIsNull(Long userId) {
        if (userId == null) {
            log.warn("Не найден пользователь с id null");
            throw new UserNotFoundException("id пользователя не был получен");
        }
    }

    private void validateUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
