package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findByOwnerId(Long userId, PageRequest pageRequest);

    Collection<Item> findByNameContainsOrDescriptionContainsIgnoreCase(String nameText, String descriptionText,
                                                                       PageRequest pageRequest);

    Boolean existsByOwnerId(Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    Optional<Item> findByIdAndOwner_Id(Long id, Long ownerId);
}
