package org.eatclub.challenge.domain.restaurant.dto;

import java.util.List;

public record RestaurantResponse(
        List<RestaurantDto> restaurants
) {
}