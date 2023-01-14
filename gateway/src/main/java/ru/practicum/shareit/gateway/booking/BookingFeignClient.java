package ru.practicum.shareit.gateway.booking;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateDto;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;

import java.util.List;

@FeignClient(value = "bookingClient", path = "/bookings/items", url = "${SHAREIT_SERVER_URL}")
@Headers("X-Sharer-User-Id: {userId}")
public interface BookingFeignClient {

    @PostMapping()
    BookingDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingCreateDto bookingCreateDto);

    @PatchMapping(path = "{bookingId}")
    BookingDto update(
            @PathVariable("bookingId") long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId);

    @GetMapping("{bookingId}")
    BookingDto getBookingById(
            @PathVariable("bookingId") long bookingId, @RequestHeader("X-Sharer-User-Id") long userId);

    @GetMapping("?from={from}&size={size}&state={state}")
    List<BookingDto> getBookingByState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable String from,
            @PathVariable String size,
            @PathVariable String state);

    @GetMapping("/owner?from={from}&size={size}&state={state}")
    List<BookingDto> getItemsByStateAndOwner(
            @PathVariable String from,
            @PathVariable String size,
            @PathVariable String state,
            @RequestHeader("X-Sharer-User-Id") long userId);

}
