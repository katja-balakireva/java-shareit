package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDto> getAll() {
        return null;
    }

    public ItemDto getById(Long itemId) {
        return null;
    }

    public ItemDto addItem(Long userId, ItemDto item) {
        User owner = null;


        return null;
    }

    public ItemDto updateItem(Long itemId) {
        return null;
    }


    public void deleteItem(Long itemId) {
    }

    public ItemDto searchItem(String text) {
        return null;
    }
}
