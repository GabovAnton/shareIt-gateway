package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;

class SearchRejected implements BookingSearch {

    @Override
    public BooleanExpression getSearchExpression(long userId) {
        QBooking qBooking = QBooking.booking;

        return qBooking.status.eq(BookingStatus.REJECTED);
    }
}
