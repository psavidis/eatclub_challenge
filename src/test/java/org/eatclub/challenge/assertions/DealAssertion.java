package org.eatclub.challenge.assertions;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.fail;
import static org.eatclub.challenge.ActiveDealsResponseDTO.DealDto;

public class DealAssertion {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private final DealDto deal;

    private DealAssertion(DealDto deal) {
        this.deal = deal;
    }

    public static DealAssertion assertThat(DealDto deal) {
        return new DealAssertion(deal);
    }

    public DealAssertion hasOpenRestaurantAtTimeOfDay(String timeOfDay) {
        LocalTime time = toLocalTime(timeOfDay);

        LocalTime restaurantOpen = toLocalTime(deal.restaurantOpen());
        LocalTime restaurantClosed = toLocalTime(deal.restaurantClose());

        boolean isRestaurantOpen = !time.isBefore(restaurantOpen)
                && !time.isAfter(restaurantClosed);

        if (!isRestaurantOpen) {
            fail("Deal assertion failed: Restaurant: " + deal.restaurantObjectId() + " is not open at " + timeOfDay
                    + ". Open: " + deal.restaurantOpen()
                    + ", Close: " + deal.restaurantClose());
        }

        return this;
    }

    private LocalTime toLocalTime(String time) {
        return LocalTime.parse(time, FORMATTER);
    }

}