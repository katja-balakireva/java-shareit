package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {

    private Set<Item> items;
    private Long idCounter;

    public ItemRepository() {
        this.items = new LinkedHashSet<>();
        this.idCounter = 0L;
    }

    public Item addItem(Item item) {
        Long itemId = ++idCounter;
        item.setId(itemId);
        items.add(item);
        return getById(itemId);
    }

    public Item getById(Long itemId) {
       return items.stream()
                .filter(item -> itemId.equals(item.getId()))
                .findAny().orElse(null);
    }

    public List<Item> getAllByUser(Long userId) {
        return items.stream()
                .filter(item -> userId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    public List<Item> getAll() {
        return items.stream()
                .collect(Collectors.toList());
    }

    public Item updateItem(Long itemId, Item item) {

        Item itemToUpdate = getById(itemId);

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    public void deleteItem(Long itemId) {
        Item itemToRemove = getById(itemId);
        items.remove(itemToRemove);
    }

    public List<Item> searchItem(String text) {
        final String str = text.toLowerCase();
        Predicate<Item> isName = item -> item.getName()
                .toLowerCase()
                .contains(str);
        Predicate<Item> isDescription = item -> item.getDescription()
                .toLowerCase()
                .contains(str);
        if (text.isEmpty() || text.isBlank()) {
            return Collections.<Item>emptyList();
        } else return items.stream()
                .filter(isName.or(isDescription))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
