package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemService;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.user.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = {"classpath:/schema.sql", "classpath:/SampleData.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private final EntityManager em;
    LocalDateTime currentDate = LocalDateTime
            .of(2022, 12, 10, 5, 5, 5, 5);
    @Mock
    BookingSearchFactory bookingSearchFactory;
    @Mock
    private BookingRepository bookingMockRepository;
    @Mock
    private UserService userMockService;
    @Mock
    private ItemService itemMockService;
    @InjectMocks
    private BookingService bookingService = new BookingServiceImpl(bookingMockRepository, userMockService, itemMockService, bookingSearchFactory);
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void getBookingWhenBookingNotFoundThenEntityNotFoundExceptionThrown() {
        long bookingId = 0L;

        when(bookingMockRepository.findById(bookingId)).thenThrow(new EntityNotFoundException("Booking with id " + bookingId + " not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            bookingMockRepository.findById(bookingId);
        });

    }

    @Test
    void getBookingWhenBookingAccessedByUnauthorizedPersonThenEntityNotFoundExceptionThrown() {
        Long requesterId = 10L;

        User owner = new User();

        owner.setId(100L);

        Booking booking = makeBooking();

        booking.setId(100L);

        booking.getBooker().setId(100L);

        booking.getItem().setOwner(owner);

        assertThrows(EntityNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(bookingService, "checkItemOwner", booking, requesterId);
        });

    }

    @Test
    void saveWhenEndLessThanStartFailAndShouldNotInvokeRepository() {

        Booking booking = makeBooking();

        booking.setStart(currentDate.plusYears(1L));

        booking.setEnd(currentDate.minusYears(1L));

        assertThrows(ForbiddenException.class, () -> {
            bookingService.save(booking, 1L);
        });

        Mockito.verify(bookingMockRepository, never()).save(booking);

        Mockito.verify(bookingMockRepository, never()).searchBookingsById(booking.getItem().getId(), booking.getBooker().getId());

        Mockito.verify(itemMockService, never()).isItemAvailable(booking.getItem().getId());

    }

    @Test
    void saveWhenTryToBookTheSameItemTwiceShouldNotInvokeRepository() {

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        Booking booking = makeBooking();

        booking.setStart(LocalDateTime.now().plusMinutes(15));

        booking.setEnd(LocalDateTime.now().plusYears(15));

        List<Booking> bookingsFromRepository = List.of(booking);

        when(bookingMockRepository.searchBookingsById(anyLong(), anyLong())).thenReturn(bookingsFromRepository);

        assertThrows(EntityNotFoundException.class, () -> {
            bookingService.save(booking, 1L);
        });

        Mockito.verify(itemMockService, never()).isItemAvailable(booking.getItem().getId());

        Mockito.verify(bookingMockRepository, never()).save(booking);

    }

    @Test
    void saveShouldNotThrowException() {

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        ReflectionTestUtils.setField(bookingService, "itemService", itemMockService);

        Booking booking = makeBooking();

        booking.setStart(LocalDateTime.now().plusMinutes(15));

        booking.setEnd(LocalDateTime.now().plusYears(15));

        bookingService.save(booking, 100L);

        Mockito.verify(bookingMockRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertThat(savedBooking, equalTo(booking));
    }

    @Test
    void changeBookingStatusToWrongBookingShouldThrowException() {

        long bookingId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        when(bookingMockRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Booking with id " + bookingId + " not found"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.changeBookingStatus(bookingId, true, 100L);
        });

        assertThat(entityNotFoundException.getMessage(), equalTo("Booking with id " + bookingId + " not found"));

        Mockito.verify(bookingMockRepository, never()).save(any());

    }

    @Test
    void changeBookingStatusByWrongOwnerBookingShouldThrowException() {

        Booking booking = makeBooking();

        booking.getItem().getOwner().setId(200L);

        long bookingId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        when(bookingMockRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.changeBookingStatus(bookingId, true, 100L);
        });

        assertThat(entityNotFoundException.getMessage(), equalTo("Booking status could be changed only by owner"));

        Mockito.verify(bookingMockRepository, never()).save(any());

    }

    @Test
    void changeBookingStatusToSameStatusShouldThrowException() {

        Booking booking = makeBooking();

        booking.setStatus(BookingStatus.APPROVED);

        long bookingId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        when(bookingMockRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            bookingService.changeBookingStatus(bookingId, true, 100L);
        });

        assertThat(forbiddenException.getMessage(), equalTo("Booking status has already been changed"));

        Mockito.verify(bookingMockRepository, never()).save(any());

    }

    @Test
    void changeBookingStatusShouldReturnSavedObject() {

        Booking booking = makeBooking();

        booking.setStart(LocalDateTime.now().plusMinutes(15));

        booking.setEnd(LocalDateTime.now().plusYears(15));

        long bookingId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        when(bookingMockRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.changeBookingStatus(bookingId, true, 100L);

        Mockito.verify(bookingMockRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertThat(savedBooking, equalTo(booking));

        Mockito.verify(bookingMockRepository, times(1)).save(any());

    }

    @Test
    void getBookingByStateByWrongUserShouldThrowException() {

        long ownerId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        ReflectionTestUtils.setField(bookingService, "userService", userMockService);

        when(userMockService.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.getBookingByState(1, 5, 100L, "APPROVED");
        });

        assertThat(entityNotFoundException.getMessage(), equalTo("user with id: " + ownerId + " not found"));
    }

    @Test
    void getBookingByStateUnknownStateShouldThrowException() {

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        ReflectionTestUtils.setField(bookingService, "userService", userMockService);

        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);

        when(userMockService.existsById(anyLong())).thenReturn(true);

        IllegalArgumentException IllegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingByState(1, 5, 100L, "UNKNOWN");
        });

        assertThat(IllegalArgumentException.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void getBookingByStateAndOwnerByWrongUserShouldThrowException() {

        long ownerId = 100L;

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        ReflectionTestUtils.setField(bookingService, "userService", userMockService);

        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);

        when(userMockService.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.getBookingByStateAndOwner(1, 5, 100L, "APPROVED");
        });

        assertThat(entityNotFoundException.getMessage(), equalTo("user with id: " + ownerId + " not found"));
    }

    @Test
    void getBookingByStateAndOwnerUnknownStateShouldThrowException() {

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingMockRepository);

        ReflectionTestUtils.setField(bookingService, "userService", userMockService);

        ReflectionTestUtils.setField(bookingService, "bookingSearchFactory", bookingSearchFactory);

        when(userMockService.existsById(anyLong())).thenReturn(true);

        IllegalArgumentException IllegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingByStateAndOwner(1, 5, 100L, "UNKNOWN");
        });

        assertThat(IllegalArgumentException.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    private Booking makeBooking() {
        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setStart(currentDate.minusDays(2));
        booking.setEnd(currentDate.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(new Item());
        booking.getBooker().setId(100L);
        booking.getItem().setId(100L);
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(100L);

        return booking;
    }
}