package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDateTime;

class SearchPast implements BookingSearch {

    @Override
    public BooleanExpression getSearchExpression(long userId) {
        QBooking qBooking = QBooking.booking;
        BooleanExpression expressionOne = qBooking.start.before(LocalDateTime.now());
        BooleanExpression expressionTwo = qBooking.end.before(LocalDateTime.now());

        return expressionOne.and(expressionTwo);
    }

}
