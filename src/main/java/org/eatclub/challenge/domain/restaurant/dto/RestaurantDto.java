package org.eatclub.challenge.domain.restaurant.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eatclub.challenge.jackson.CaseInsensitiveLocalTimeDeserializer;

import java.time.LocalTime;
import java.util.List;

public record RestaurantDto(
        String objectId,
        String name,
        String address1,
        String suburb,
        List<String> cuisines,
        String imageLink,
        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime open,
        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime close,
        List<DealDto> deals
) {
}