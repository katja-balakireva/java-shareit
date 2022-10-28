package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId (Long userId);
    Collection<Item> findByNameContainsOrDescriptionContainsIgnoreCase(String nameText, String descriptionText);
    Boolean existsByOwnerId(Long ownerId);
}
