package ru.practicum.shareit.server.booking;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BookingSearchFactory {

    Map<String, BookingSearch> searchMap = new HashMap<>() {{
        put("ALL", new SearchAll());
        put("CURRENT", new SearchCurrent());
        put("PAST", new SearchPast());
        put("FUTURE", new SearchFuture());
        put("WAITING", new SearchWaiting());
        put("REJECTED", new SearchRejected());
    }};

    public Optional<BookingSearch> getSearchMethod(String operator) {

        return Optional.ofNullable(searchMap.get(operator));
    }

}
