package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {

    private Map<Long, Item> itemMap;
    private Long idCounter;

    public ItemRepository() {
        this.itemMap = new HashMap<>();
        this.idCounter = 0L;
    }

    private void saveToMap(Item item) {
        itemMap.put(++idCounter, item);
    }
}
