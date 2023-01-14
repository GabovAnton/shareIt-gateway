package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Service;

@Service
class SearchAll implements BookingSearch {

    @Override
    public BooleanExpression getSearchExpression(long userId) {
        QBooking qBooking = QBooking.booking;
        return qBooking.id.isNotNull();
    }
}
