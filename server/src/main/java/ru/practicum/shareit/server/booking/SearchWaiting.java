package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;

class SearchWaiting implements BookingSearch {

    @Override
    public BooleanExpression getSearchExpression(long userId) {
        QBooking qBooking = QBooking.booking;

        return qBooking.status.eq(BookingStatus.WAITING);
    }
}
