package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @PostMapping()
    public BookingDto create( @RequestBody BookingCreateDto bookingCreateDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {

        Booking booking = bookingMapper.bookingCreateDtoToBooking(bookingCreateDto, userId);
        Booking savedBooking = bookingService.save(booking, userId);

        return bookingMapper.bookingToBookingDto(savedBooking);
    }

    @PatchMapping("{bookingId}")
    public BookingDto update(@PathVariable long bookingId,
                             @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        Booking booking = bookingService.changeBookingStatus(bookingId, approved, userId);
        return bookingMapper.bookingToBookingDto(booking);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        Booking booking = bookingService.getBooking(userId, bookingId);

        return ResponseEntity.ok(bookingMapper.bookingToBookingDto(booking));

    }

    @GetMapping("")
    public List<BookingDto> getBookingByState(
            @RequestParam(required = false)  Integer from,
            @RequestParam(required = false)  Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingByState(from, size, userId, state);

    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByStateAndOwner(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false)  Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        return bookingService.getBookingByStateAndOwner(from, size, userId, state);

    }

}
