package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

   // Collection<Booking> findById(Long bookingId);
}
