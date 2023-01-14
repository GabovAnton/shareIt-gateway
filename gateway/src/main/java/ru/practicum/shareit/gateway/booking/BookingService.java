package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateDto;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

import java.util.List;

@Service
@RequiredArgsConstructor
//@CacheConfig(cacheNames = {"bookings"})
public class BookingService {

    private final BookingFeignClient bookingFeignClient;

    /*@Caching(evict = {
            @CacheEvict(value = "items"*//*, key = "{#userId + #bookingCreateDto.itemId}"*//*)},
            put = {@CachePut(key = "{#userId + #bookingCreateDto.id }")})*/
   // @CacheEvict(value = "items")
    public BookingDto create(long userId, BookingCreateDto bookingCreateDto) {

        return bookingFeignClient.create(userId, bookingCreateDto);
    }

   /* @Caching(evict = {
            @CacheEvict(value = "items"*//*, key = "{#bookingCreateDto.itemId + #userId }"*//*),
            @CacheEvict(key = "{#userId, #result.id }")})*/
 //  @CacheEvict(value = "items")
   public BookingDto update(long userId, long bookingId, Boolean approved) {

        return bookingFeignClient.update(bookingId, approved, userId);
    }

/*
    @Cacheable(key = "{#userId + #bookingId }")
*/
//@CacheEvict(value = "items")
public BookingDto getBooking(long userId, long bookingId) {

        return bookingFeignClient.getBookingById(bookingId, userId);
    }

/*
    @Cacheable(key = "{#userId + #state }")
*/
//@CacheEvict(value = "items")
public List<BookingDto> getBookingByState(Long userId, Integer from, Integer size, BookingState state) {

        return bookingFeignClient.getBookingByState(userId, from.toString(), size.toString(), state.name());

    }

/*
    @Cacheable(key = "{#userId + #state }")
*/
//@CacheEvict(value = "items")
public List<BookingDto> getItemsByStateAndOwner(Long userId, Integer from, Integer size, BookingState state) {

        return bookingFeignClient.getItemsByStateAndOwner(from.toString(), size.toString(), state.name(), userId);

    }

}
