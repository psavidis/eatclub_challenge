package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

/**
 * Represents a deal with a start and end time.
 * Provides methods to check if the deal is active at a given time.
 */
public class Deal {

    private static final Logger LOG = LoggerFactory.getLogger(Deal.class);

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
        LocalTime restaurantOpen = restaurantDto.open();
        LocalTime restaurantClose = restaurantDto.close();

        // Prefer start if present and within restaurant hours
        if (dealDto.start() != null) {
            LocalTime dealStart = dealDto.start();

            if (timeFieldFallsWithinWindow(dealStart, restaurantOpen, restaurantClose)) {
                return dealStart;
            }

            LOG.warn("Deal {} start time {} is outside restaurant hours {}–{}; using restaurant open",
                    dealDto.objectId(), dealStart, restaurantOpen, restaurantClose);
        }

        // Deal has open/close — but per policy, prefer restaurant open
        if (dealDto.open() != null || dealDto.close() != null) {
            LOG.info("Deal {} defines open/close but restaurant open {} is preferred as per policy",
                    dealDto.objectId(), restaurantOpen);
            return restaurantOpen;
        }

        // Fallback: no deal time info, use restaurant open
        LOG.warn("Deal {} has no time info; falling back to restaurant open {}", dealDto.objectId(), restaurantOpen);
        return restaurantOpen;
    }

    private static LocalTime getDealEnd(DealDto dealDto, RestaurantDto restaurantDto) {
        LocalTime restaurantOpen = restaurantDto.open();
        LocalTime restaurantClose = restaurantDto.close();

        // Prefer deal end if present and within restaurant hours
        if (dealDto.end() != null) {
            LocalTime dealEnd = dealDto.end();

            if (timeFieldFallsWithinWindow(dealEnd, restaurantOpen, restaurantClose)) {
                return dealEnd;
            }

            LOG.warn("Deal {} end time {} is outside restaurant hours {}–{}; using restaurant close",
                    dealDto.objectId(), dealEnd, restaurantOpen, restaurantClose);
        }

        // Deal has close field — but per policy, prefer restaurant close
        if (dealDto.close() != null) {
            LOG.info("Deal {} defines close field but restaurant close {} is preferred as per policy",
                    dealDto.objectId(), restaurantClose);
            return restaurantClose;
        }

        // Fallback: no deal end info, use restaurant close
        LOG.warn("Deal {} has no end/close info; falling back to restaurant close {}", dealDto.objectId(), restaurantClose);
        return restaurantClose;
    }

    /**
     * Returns true if the deal is active at the given time.
     */
    public boolean isActive(LocalTime time) {
        return !hasIncompleteTimes() && timeFieldFallsWithinWindow(time, start, end);
    }

    public DealDetails getDetails() {
        return details;
    }

    private boolean hasIncompleteTimes() {
        return start == null || end == null;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    private static boolean timeFieldFallsWithinWindow(LocalTime time, LocalTime windowStart, LocalTime windowEnd) {
        return !time.isBefore(windowStart) && !time.isAfter(windowEnd);
    }

}