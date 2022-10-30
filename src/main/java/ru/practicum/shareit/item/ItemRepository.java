package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByOwnerId(Long userId);

    Collection<Item> findByNameContainsOrDescriptionContainsIgnoreCase(String nameText, String descriptionText);

    Boolean existsByOwnerId(Long ownerId);
}
