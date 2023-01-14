package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    private final BookingSearchFactory bookingSearchFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Booking save(Booking booking, long userId) {

        checkBookingBasicConstraints(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        log.debug("Bookings for user id: {} saved: {}", userId, booking);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeBookingStatus(long bookingId, Boolean isApproved, long requesterId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException(
                "Booking with id " + bookingId + " not found"));

        if (!booking.getItem().getOwner().getId().equals(requesterId)) {
            throw new EntityNotFoundException("Booking status could be changed only by owner");
        }
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus().equals(newStatus)) {
            throw new ForbiddenException("Booking status has already been changed");
        } else {
            booking.setStatus(newStatus);
            bookingRepository.save(booking);
            log.debug("Bookings  id: {} change status to: {}", bookingId, newStatus);
            return booking;
        }
    }

    @Override
    public Booking getBooking(long requesterId, long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException(
                "Booking with id " + bookingId + " not found"));

        checkItemOwner(booking, requesterId);
        return booking;
    }

    @Override
    public List<BookingDto> getBookingByState(Integer from, Integer size, long userId, String state) {

        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("user with id: " + userId + " not found");
        } else {

            BookingSearch
                    bookingSearch =
                    bookingSearchFactory.getSearchMethod(state).orElseThrow(() -> new IllegalArgumentException(
                            "Unknown state: UNSUPPORTED_STATUS"));

            QBooking qBooking = QBooking.booking;
            JPAQuery<Booking> query = new JPAQuery<>(entityManager);

            long totalItems = bookingRepository.count() + 1;
            int offset = from != null ? from : 0;

            BooleanExpression expressionBooker = qBooking.booker.id.eq(userId);

            List<BookingDto>
                    collect =
                    query
                            .from(qBooking)
                            .where(expressionBooker.and(bookingSearch.getSearchExpression(userId)))
                            .orderBy(qBooking.start.desc())
                            .limit(size != null ? size : totalItems)
                            .offset(offset)
                            .fetch()
                            .stream()
                            .map(BookingMapper.INSTANCE::bookingToBookingDto)
                            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

            log.debug("Bookings for owner id: {} and state: {} returned collection: {}", userId, state, collect);

            return collect;
        }
    }

    @Override
    public List<BookingDto> getBookingByStateAndOwner(Integer from, Integer size, long ownerId, String state) {

        if (!userService.existsById(ownerId)) {
            throw new EntityNotFoundException("user with id: " + ownerId + " not found");
        } else {
            BookingSearch
                    bookingSearch =
                    bookingSearchFactory.getSearchMethod(state).orElseThrow(() -> new IllegalArgumentException(
                            "Unknown state: UNSUPPORTED_STATUS"));

            QBooking qBooking = QBooking.booking;
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            long TotalItems = bookingRepository.count() + 1;
            int offset = from != null ? from : 0;
            List<BookingDto> collect = queryFactory
                    .selectFrom(qBooking)
                    .where(qBooking.item.owner.id
                            .eq(ownerId)
                            .and(bookingSearch.getSearchExpression(ownerId)))
                    .orderBy(qBooking.start.desc())
                    .limit(size != null ? size : TotalItems)
                    .offset(offset)
                    .fetch()
                    .stream()
                    .map(BookingMapper.INSTANCE::bookingToBookingDto)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

            log.debug("Bookings for owner id: {} and state: {} returned collection: {}", ownerId, state, collect);

            return collect;

        }
    }

    private Boolean checkItemOwner(Booking booking, Long requesterId) {

        if (!booking.getBooker().getId().equals(requesterId) &&
            !booking.getItem().getOwner().getId().equals(requesterId)) {
            throw new EntityNotFoundException("Booking could be retrieved only by items owner or booking author");
        }
        return true;
    }

    private void checkBookingBasicConstraints(Booking booking, Long requesterId) {

        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isBefore(LocalDateTime.now()) ||
            booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Booking start should be less than End and not be in past");
        }
        List<Booking> bookings = bookingRepository.searchBookingsById(booking.getItem().getId(), requesterId);

        if (bookings.stream().anyMatch(b -> b.getStatus().equals(BookingStatus.WAITING) ||
                                            b.getStatus().equals(BookingStatus.APPROVED))) {
            throw new EntityNotFoundException("Booking can't be made to one item more than one time");
        }

        itemService.isItemAvailable(booking.getItem().getId());
    }

}
