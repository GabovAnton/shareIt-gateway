package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.user.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = {"classpath:/schema.sql", "classpath:/SampleData.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class BookingIntegrationTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Autowired
    private UserRepository userRepository;

    private final UserService userService = new UserServiceImpl(userRepository, userMapper);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingSearchFactory bookingSearchFactory;

    private final BookingService bookingService = new BookingServiceImpl(bookingRepository, userService, itemService,
                                                                         bookingSearchFactory
    );

    @Test
    public void getBookingsAllShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "ALL");

        assertThat(all, hasSize(7));

    }

    @Test
    public void getBookingsAllWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 1L, "ALL");

        assertThat(all, hasSize(1));

    }

    @Test
    public void getBookingsWaitingWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 1L, "WAITING");

        assertThat(all, hasSize(1));

    }

    @Test
    public void getBookingsWaitingShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "WAITING");

        assertThat(all, hasSize(2));

    }

    @Test
    public void getBookingsRejectedShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "REJECTED");

        assertThat(all, hasSize(1));
    }

    @Test
    public void getBookingsRejectedWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 4L, "REJECTED");

        assertThat(all, hasSize(1));
    }

    @Test
    public void getBookingsInFutureShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "FUTURE");

        assertThat(all, hasSize(1));

        assertThat(all.stream().anyMatch(x -> x.getStart().isAfter(LocalDateTime.now())), is(true));
        assertThat(all.stream().anyMatch(x -> x.getEnd().isAfter(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsInFutureWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 1L, "FUTURE");

        assertThat(all, hasSize(1));

        assertThat(all.stream().anyMatch(x -> x.getStart().isAfter(LocalDateTime.now())), is(true));
        assertThat(all.stream().anyMatch(x -> x.getEnd().isAfter(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsInPastShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "PAST");

        assertThat(all, hasSize(3));

        assertThat(all.stream().allMatch(x -> x.getStart().isBefore(LocalDateTime.now())), is(true));
        assertThat(all.stream().allMatch(x -> x.getEnd().isBefore(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsInPastWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 2L, "PAST");

        assertThat(all, hasSize(2));

        assertThat(all.stream().allMatch(x -> x.getStart().isBefore(LocalDateTime.now())), is(true));
        assertThat(all.stream().allMatch(x -> x.getEnd().isBefore(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsInCurrentShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByState(0, 10, 1L, "CURRENT");

        assertThat(all, hasSize(3));

        assertThat(all.stream().allMatch(x -> x.getStart().isBefore(LocalDateTime.now())), is(true));
        assertThat(all.stream().allMatch(x -> x.getEnd().isAfter(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsInCurrentWithOwnerShouldReturnList() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        List<BookingDto> all = bookingService.getBookingByStateAndOwner(0, 10, 3L, "CURRENT");

        assertThat(all, hasSize(2));

        assertThat(all.stream().allMatch(x -> x.getStart().isBefore(LocalDateTime.now())), is(true));
        assertThat(all.stream().allMatch(x -> x.getEnd().isAfter(LocalDateTime.now())), is(true));

    }

    @Test
    public void getBookingsShouldReturnEntity() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        Booking booking = bookingService.getBooking(1, 1);

        assertThat(booking.getId(), equalTo(1L));

    }

    @Test
    public void changeBookingStatusShouldThrowForbiddenException() {

        ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "entityManager", entityManager);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            bookingService.changeBookingStatus(4, true, 2);
        });
        assertThat(forbiddenException.getMessage(), equalTo("Booking status has already been changed"));

    }

}
