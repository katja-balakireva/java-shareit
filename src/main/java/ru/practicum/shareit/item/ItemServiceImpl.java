package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidateOwnershipException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Component("DefaultItemService")
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> result;
        if (userId == null) {
            result = itemRepository.getAll().stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            log.info("Получен список из {} вещей: {}", result.size(), result);
        } else {
            result = itemRepository.getAllByUser(userId).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            log.info("Получен список из {} вещей пользователя {}: {}", result.size(), userId, result);
        }
        return result;
    }

    public ItemDto getById(Long itemId) {
        Item item = itemRepository.getById(itemId);
        validate(item);
        ItemDto result = ItemMapper.toItemDto(item);
        log.info("Получена вещь с id {}: {}", itemId, result);
        return result;
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        validate(user);
        item.setOwner(user);
        Item itemToAdd = itemRepository.addItem(item);
        ItemDto result = ItemMapper.toItemDto(itemToAdd);
        log.info("Пользователь {} добавил новую вещь: {}", userId, result);
        return result;
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Long ownerId = itemRepository.getById(itemId).getOwner().getId();
        validate(ownerId, userId);
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        validate(user);
        item.setId(itemId);
        item.setOwner(user);
        Item itemToUpdate = itemRepository.updateItem(itemId, item);
        validate(itemToUpdate);
        ItemDto result = ItemMapper.toItemDto(itemToUpdate);
        log.info("Пользователь {} обновил вещь с id {}: {}", userId, itemId, result);
        return result;
    }

    public void deleteItem(Long itemId) {
        Item item = itemRepository.getById(itemId);
        validate(item);
        log.info("Вещь с id {} удалена", itemId);
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItem(String text) {
        List<ItemDto> result = itemRepository.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Найденные вещи {}: ", result);
        return result;
    }

    /* VALIDATION METHODS */

    private void validate(Long ownerId, Long userId) {
        if (!userId.equals(ownerId)) {
            log.warn("Выброшено исключение ValidateOwnershipException");
            throw new ValidateOwnershipException("Обновлять предмет может только владелец");
        }
    }

    private void validate(Item itemToValidate) {
        if (itemToValidate == null) {
            log.warn("Выброшено исключение ItemNotFoundException");
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (itemRepository.getById(itemToValidate.getId()) == null) {
            log.warn("Выброшено исключение ItemNotFoundException");
            throw new ItemNotFoundException("Вещь не найдена");
        }
    }

    private void validate(User userToValidate) {
        if (userToValidate == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (userRepository.getById(userToValidate.getId()) == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
