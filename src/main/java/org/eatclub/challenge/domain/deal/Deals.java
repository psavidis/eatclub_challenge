package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;

import java.util.List;
import java.util.Map;

public class Deals {

    /**
     * Factory method to create a list of deals from a list of restaurants DTOs.
     */
    public static List<Deal> fromRestaurants(List<RestaurantDto> restaurants) {
        return restaurants.stream()
                .flatMap(restaurant -> restaurant.deals().stream()
                        .map(deal -> Map.entry(deal, restaurant))
                ).map(entry -> Deal.of(entry.getKey(), entry.getValue()))
                .toList();
    }

}
