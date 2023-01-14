package ru.practicum.shareit.server.booking;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDateTime;

class SearchFuture implements BookingSearch {

    @Override
    public BooleanExpression getSearchExpression(long userId) {

        QBooking qBooking = QBooking.booking;
        BooleanExpression expressionOne = qBooking.start.after(LocalDateTime.now());
        BooleanExpression expressionTwo = qBooking.end.after(LocalDateTime.now());

        BooleanExpression booleanExpression = expressionOne.and(expressionTwo);
        return booleanExpression;

    }

}
