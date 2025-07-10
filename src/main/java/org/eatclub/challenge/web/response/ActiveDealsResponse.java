package org.eatclub.challenge.web.response;

import org.eatclub.challenge.domain.deal.Deal;

import java.util.List;

public record ActiveDealsResponse(List<DealDto> deals) {
    record DealDto(
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

    public static class Builder {

        private List<Deal> activeDeals;

        public Builder() {
        }

        public Builder withActiveDeals(List<Deal> activeDeals) {
            this.activeDeals = activeDeals;
            return this;
        }

        public ActiveDealsResponse build() {
            List<DealDto> deals = activeDeals.stream()
                    .map(this::toDto)
                    .toList();

            return new ActiveDealsResponse(deals);
        }

        private DealDto toDto(Deal deal) {
            var details = deal.getDetails();
            return new DealDto(
                    details.getRestaurantObjectId(),
                    details.getRestaurantName(),
                    details.getRestaurantAddress1(),
                    details.getRestaurantSuburb(),
                    details.getRestaurantOpen(),
                    details.getRestaurantClose(),
                    details.getDealObjectId(),
                    details.getDiscount(),
                    details.getDineIn(),
                    details.getLightning(),
                    details.getQtyLeft()
            );
        }
    }

}