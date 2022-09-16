package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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

    public List<ItemDto> getAll() {
        List<ItemDto> result = itemRepository.getAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получен список из {} вещей: {}", result.size(), result);
        return result;
    }

    public List<ItemDto> getAllByUser(Long userId) {
        List<ItemDto> result = itemRepository.getAllByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получен список из {} вещей пользователя {}: {}", result.size(), userId, result);
        return result;
    }

    public ItemDto getById(Long itemId) {
        Item item = itemRepository.getById(itemId);
        ItemDto result = ItemMapper.toItemDto(item);
        log.info("Получена вещь с id {}: {}", itemId, result);
        return result;
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        if (user == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwner(user);
        Item itemToAdd = itemRepository.addItem(item);
        ItemDto result = ItemMapper.toItemDto(itemToAdd);
        log.info("Пользователь {} добавил новую вещь: {}", userId, result);
        return result;
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Long ownerId = itemRepository.getById(itemId).getOwner().getId();

        if (!userId.equals(ownerId)) {
            log.warn("Выброшено исключение ValidateOwnershipException");
            throw new ValidateOwnershipException("Обновлять предмет может только владелец");
        }

        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        if (user == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setId(itemId);
        item.setOwner(user);
        Item itemToUpdate = itemRepository.updateItem(itemId, item);
        ItemDto result = ItemMapper.toItemDto(itemToUpdate);
        log.info("Пользователь {} обновил вещь с id {}: {}", userId, itemId, result);
        return result;
    }

    public void deleteItem(Long itemId) {
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
}
