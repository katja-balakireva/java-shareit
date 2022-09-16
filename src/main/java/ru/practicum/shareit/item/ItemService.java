package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidateOwnershipException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<ItemDto> getAll(){
        return itemRepository.getAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAllByUser(Long userId){
        return itemRepository.getAllByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getById(Long itemId) {
         Item item = itemRepository.getById(itemId);
         return ItemMapper.toItemDto(item);
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwner(user);
        Item itemToAdd = itemRepository.addItem(item);
        return ItemMapper.toItemDto(itemToAdd);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

       Long ownerId = itemRepository.getById(itemId).getOwner().getId();

        if (userId != ownerId) {
            throw new ValidateOwnershipException("Обновлять предмет может только владелец");
        }

        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setId(itemId);
        item.setOwner(user);
        Item itemToUpdate = itemRepository.updateItem(itemId, item);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
