package ru.practicum.shareit.server.booking;

import java.util.List;

public interface BookingService {

    Booking save(Booking booking, long userId);

    Booking changeBookingStatus(long bookingId, Boolean isApproved, long ownerId);

    Booking getBooking(long requesterId, long bookingId);

    List<BookingDto> getBookingByState(Integer from, Integer size, long ownerId, String state);

    List<BookingDto> getBookingByStateAndOwner(Integer from, Integer size, long ownerId, String state);
}

