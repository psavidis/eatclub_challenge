package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;

import java.time.LocalTime;

/**
 * Represents a deal with a start and end time.
 * Provides methods to check if the deal is active at a given time.
 */
public class Deal {

    private final LocalTime start;
    private final LocalTime end;

    private final DealDetails details;

    private Deal(LocalTime start, LocalTime end, DealDetails details) {
        this.start = start;
        this.end = end;
        this.details = details;
    }

    /**
     * Static factory method to create a Deal instance from DealDto and RestaurantDto.
     */
    public static Deal of(DealDto dealDto, RestaurantDto restaurantDto) {
        LocalTime start = getDealStart(dealDto, restaurantDto);
        LocalTime end = getDealEnd(dealDto, restaurantDto);

        DealDetails metadata = DealDetails.of(dealDto, restaurantDto);

        return new Deal(start, end, metadata);
    }

    private static LocalTime getDealStart(DealDto dealDto, RestaurantDto restaurantDto) {

        // Field Precedence: start > open > restaurant open

        if (dealDto.start() != null) {
            return dealDto.start();
        }

        if (dealDto.open() != null) {
            return dealDto.open();
        }

        return restaurantDto.open();
    }

    private static LocalTime getDealEnd(DealDto dealDto, RestaurantDto restaurantDto) {

        if (dealDto.end() != null) {
            return dealDto.end();
        }

        if (dealDto.close() != null) {
            return dealDto.close();
        }

        return restaurantDto.close();
    }

    /**
     * Returns true if the deal is active at the given time.
     */
    public boolean isActive(LocalTime time) {
        return !hasIncompleteTimes() && timeFallsWithinDealWindow(start, end, time);
    }

    public DealDetails getDetails() {
        return details;
    }

    private boolean hasIncompleteTimes() {
        return start == null || end == null;
    }

    private boolean timeFallsWithinDealWindow(LocalTime dealStart, LocalTime dealEnd, LocalTime time) {
        return !time.isBefore(dealStart) && !time.isAfter(dealEnd);
    }

}