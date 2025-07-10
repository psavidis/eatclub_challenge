package org.eatclub.challenge;

import java.util.List;

public record ActiveDealsResponseDTO(List<DealDto> deals) {
    public record DealDto(
            String restaurantObjectId,
            String restaurantName,
            String restaurantAddress1,
            String restaurantSuburb,
            String restaurantOpen,
            String restaurantClose,
            String dealObjectId,
            String discount,
            String dineIn,
            String lightning,
            String qtyLeft
    ) {}
}