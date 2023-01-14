package ru.practicum.shareit.server.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> searchBookingsByBookerInPresentTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> searchBookingsByBookerInPastTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> searchBookingsByBookerInFutureTime(Long bookerId, LocalDateTime date);

    @Query("select b from Booking b where b.id = ?1 and b.booker.id = ?2 order by b.start DESC")
    List<Booking> searchBookingsById(@NonNull Long itemId, @NonNull Long bookerId);

    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(@NonNull Long itemId,
                                                             @NonNull Long bookerId,
                                                             @NonNull BookingStatus status,
                                                             LocalDateTime end);

}