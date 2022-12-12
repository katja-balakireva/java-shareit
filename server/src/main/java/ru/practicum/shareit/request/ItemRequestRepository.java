package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest as r where r.user.id = ?1 order by r.created desc")
    List<ItemRequest> findAllByUserId(Long userId, PageRequest pageRequest);

    @Query("select r from ItemRequest as r where r.user.id <> ?1 order by r.created desc")
    List<ItemRequest> findAllOthersByUserId(Long userId, PageRequest pageRequest);
}
