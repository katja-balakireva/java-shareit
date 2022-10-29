package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //get all

    Collection<Booking> findByItemId(Long itemId);
    Collection<Booking> findAllByBookerId(Long bookerId);
   Collection<Booking> findAllByBookerIdAndItemId(Long bookerId, Long itemId);
    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, State state);

    Collection<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime);

    Collection<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime);
// queries

    @Query("select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end")
    Collection<Booking> getAllByBookerId(Long bookerId, LocalDateTime dateTime);


    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and " +
            "?2 between b.start and b.end")
    Collection<Booking> getCurrentBookingsByOwnerId(Long ownerId, LocalDateTime dateTime);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".end < ?2")
    Collection<Booking> getPastBookingsByOwnerId(Long ownerId, LocalDateTime dateTime);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".start > ?2")
    Collection<Booking> getFutureBookingsByOwnerId(Long ownerId, LocalDateTime dateTime);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1 and b" +
            ".status = ?2")
    Collection<Booking> findAllByOwnerIdAndStatus(Long ownerId, State status);

    @Query("select b from Booking b left join Item i on b.item.id = i.id where i.owner.id = ?1")
    Collection<Booking> findAllByOwnerId(Long ownerId);
}
