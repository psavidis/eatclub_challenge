package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.common.LocalTimeWindow;
import org.eatclub.challenge.domain.restaurant.RestaurantService;
import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.eatclub.challenge.web.response.ActiveDealsResponse;
import org.eatclub.challenge.web.response.PeakTimeWindowResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
     *
     * @param timeOfDayAsString the given time of day in HH:mm format
     * @return the response containing active deals
     */
    public ActiveDealsResponse getActiveDeals(String timeOfDayAsString) {
        LOG.info("getActiveDeals: {}", timeOfDayAsString);

        var timeOfDay = parseTime(timeOfDayAsString);
        var activeDeals = getActiveDeals(timeOfDay);

        return new ActiveDealsResponse.Builder()
                .withActiveDeals(activeDeals)
                .build();
    }

    /**
     * Calculates the peak time window when the most deals are active.
     * <p>
     * The algorithm works by populating all the minutes between a range (start, end).
     * Each time a deal is active, it increments the count for each minute in that range.
     * The map is sorted , allowing us to easily find the peak window.
     * <p>
     * We iterate through the timeline and find the maximum count of active deals at any minute.
     * Whenever a new maximum is found, we update the peak start and end times as we slide through the timeline.
     *
     * @param restaurants the given restaurants with their deals
     * @return the response containing the peak time window
     */
    public PeakTimeWindowResponse calculatePeakWindow(List<RestaurantDto> restaurants) {
        Map<LocalTime, Integer> timeline = new TreeMap<>();

        // Loop through each restaurant and each of its deals
        for (RestaurantDto restaurant : restaurants) {
            for (DealDto dealDto : restaurant.deals()) {
                Deal deal = Deal.of(dealDto, restaurant);

                LocalTime start = deal.getStart();
                LocalTime end = deal.getEnd();

                if (LocalTimeWindow.isValidWindow(start, end)) {
                    // Add all minutes between start and end to the timeline
                    populateMinutesForWindow(start, end, timeline);
                }
            }
        }

        int maxCount = 0;
        LocalTime peakStart = null;
        LocalTime peakEnd = null;

        for (Map.Entry<LocalTime, Integer> entry : timeline.entrySet()) {
            LocalTime time = entry.getKey();
            int count = entry.getValue();

            if (count > maxCount) {
                maxCount = count;
                peakStart = time;
                peakEnd = time;
            }
            // The count start of peak is calculated already and
            // the time is the next minute from the peak start (we are sliding)
            else if (count == maxCount && peakEnd != null && time.equals(peakEnd.plusMinutes(1))) {
                peakEnd = time; // extend the peak window
            }
        }

        return new PeakTimeWindowResponse(peakStart, peakEnd);
    }

    private void populateMinutesForWindow(LocalTime start, LocalTime end, Map<LocalTime, Integer> timeline) {
        for (LocalTime t = start; !t.isAfter(end); t = t.plusMinutes(1)) {
            timeline.merge(t, 1, Integer::sum);
        }
    }

    private List<Deal> getActiveDeals(LocalTime timeOfDay) {
        var restaurants = restaurantService.getRestaurants();
        var deals = Deals.fromRestaurants(restaurants);

        return filterActiveDeals(deals, timeOfDay);
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