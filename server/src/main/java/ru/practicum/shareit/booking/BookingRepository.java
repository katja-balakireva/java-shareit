package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //get all
    Collection<Booking> findByItemId(Long itemId);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    Collection<Booking> findAllByBookerIdAndItemId(Long bookerId, Long itemId);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, State state, PageRequest pageRequest);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime localDateTime,
                                                                       PageRequest pageRequest);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime localDateTime,
                                                                      PageRequest pageRequest);

    //queries
    @Query("select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end order by b.start desc")
    Collection<Booking> getAllByBookerId(Long bookerId, LocalDateTime dateTime, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and " +
            "?2 between b.start and b.end order by b.start desc")
    Collection<Booking> getCurrentBookingsByOwnerId(Long ownerId, LocalDateTime dateTime, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".end < ?2 order by b.start desc")
    Collection<Booking> getPastBookingsByOwnerId(Long ownerId, LocalDateTime dateTime, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".start > ?2 order by b.start desc")
    Collection<Booking> getFutureBookingsByOwnerId(Long ownerId, LocalDateTime dateTime, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".status = ?2 order by b.start desc")
    Collection<Booking> findAllByOwnerIdAndStatus(Long ownerId, State status, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 order by b.start desc")
    Collection<Booking> findAllByOwnerId(Long ownerId, PageRequest pageRequest);
}
