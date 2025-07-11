package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.common.LocalTimeWindow;
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

    private static final DealLogger DEAL_LOGGER = new DealLogger();

    private final LocalTimeWindow window;
    private final DealDetails details;

    private Deal(LocalTimeWindow window, DealDetails details) {
        this.window = window;
        this.details = details;
    }

    /**
     * Static factory method to create a Deal instance from DealDto and RestaurantDto.
     */
    public static Deal of(DealDto dealDto, RestaurantDto restaurantDto) {
        LocalTime start = getDealStart(dealDto, restaurantDto);
        LocalTime end = getDealEnd(dealDto, restaurantDto);

        DealDetails metadata = DealDetails.of(dealDto, restaurantDto);
        var window = LocalTimeWindow.of(start, end);

        return new Deal(window, metadata);
    }

    private static LocalTime getDealStart(DealDto dealDto, RestaurantDto restaurantDto) {
        LocalTime restaurantOpen = restaurantDto.open();
        LocalTime restaurantClose = restaurantDto.close();

        // Prefer start if present and within restaurant hours
        if (dealDto.start() != null) {
            var dealStart = dealDto.start();
            var dealWindow = LocalTimeWindow.of(restaurantOpen, restaurantClose);

            if (dealWindow.contains(dealStart)) {
                return dealStart;
            }
        }

        // Deal has open/close — but per policy, prefer restaurant open
        if (dealDto.open() != null) {
            LOG.info("Deal {} defines open field but restaurant open {} is preferred as per policy",
                    dealDto.objectId(), restaurantOpen);
            return restaurantOpen;
        }

        // Fallback: no deal time info, use restaurant open
        DEAL_LOGGER.logInvalidStartTime(dealDto.objectId(), restaurantOpen.toString());
        return restaurantOpen;
    }

    private static LocalTime getDealEnd(DealDto dealDto, RestaurantDto restaurantDto) {
        LocalTime restaurantOpen = restaurantDto.open();
        LocalTime restaurantClose = restaurantDto.close();

        // Prefer deal end if present and within restaurant hours
        if (dealDto.end() != null) {
            var dealEnd = dealDto.end();
            var dealWindow = LocalTimeWindow.of(restaurantOpen, restaurantClose);

            if (dealWindow.contains(dealEnd)) {
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
        DEAL_LOGGER.logInvalidEndTime(dealDto.objectId(), restaurantClose.toString());
        return restaurantClose;
    }

    /**
     * Returns true if the deal is active at the given time.
     */
    public boolean isActive(LocalTime time) {
        return window.contains(time);
    }

    public DealDetails getDetails() {
        return details;
    }

    public LocalTime getStart() {
        return window.getStart();
    }

    public LocalTime getEnd() {
        return window.getEnd();
    }

}