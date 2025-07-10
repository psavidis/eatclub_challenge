package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.RestaurantService;
import org.eatclub.challenge.web.response.ActiveDealsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Service for managing deals.
 * It retrieves active deals based on the provided time of day.
 */
@Service
public class DealService {

    private static final Logger LOG = LoggerFactory.getLogger(DealService.class);

    private static final DateTimeFormatter HOURS_MINUTES_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private final RestaurantService restaurantService;

    public DealService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Returns a list of active deals based on the provided time of day.
     * @param timeOfDayAsString the given time of day in HH:mm format
     * @return the response containing active deals
     */
    public ActiveDealsResponse getActiveDeals(String timeOfDayAsString) {
        LOG.info("getActiveDeals: {}", timeOfDayAsString);

        LocalTime timeOfDay = parseTime(timeOfDayAsString);

        var restaurants = restaurantService.getRestaurants();
        var deals = Deals.fromRestaurants(restaurants);

        List<Deal> activeDeals = filterActiveDeals(deals, timeOfDay);

        return new ActiveDealsResponse.Builder()
                .withActiveDeals(activeDeals)
                .build();
    }

    private List<Deal> filterActiveDeals(List<Deal> deals, LocalTime timeOfDay) {
        return deals.stream()
                .filter(deal -> deal.isActive(timeOfDay))
                .toList();

    }

    private LocalTime parseTime(String timeOfDay) {
        try {
            return LocalTime.parse(timeOfDay, HOURS_MINUTES_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Please use HH:mm format.", e);
        }
    }

}