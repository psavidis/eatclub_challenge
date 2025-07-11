package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DealTest {

    // deal start

    @Test
    void shouldReturn_DealStart_When_WithinRestaurantHours() {
        // given
        var restaurant = restaurant(
                LocalTime.of(12, 0), // restaurant open
                LocalTime.of(14, 0), // restaurant close
                null,
                null,
                LocalTime.of(13, 0), // deal start (within restaurant hours)
                null
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getStart()).isEqualTo(LocalTime.of(13, 0));
    }

    @Test
    void shouldReturn_RestaurantOpen_When_StartIsOutsideRestaurantHours() {
        // given
        var restaurant = restaurant(
                LocalTime.of(12, 0), // restaurant open
                LocalTime.of(14, 0),
                null,
                null,
                LocalTime.of(10, 0), // deal start (before restaurant open)
                null
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getStart()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void shouldReturn_RestaurantOpen_When_DealOpenCloseAreDefined() {
        // given
        var restaurant = restaurant(
                LocalTime.of(11, 0), // restaurant open
                LocalTime.of(23, 0),
                LocalTime.of(12, 0), // deal open
                LocalTime.of(14, 0), // deal close
                null,
                null
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getStart()).isEqualTo(LocalTime.of(11, 0));
    }

    @Test
    void shouldReturn_RestaurantOpen_When_NoTimeProvided() {
        // given
        var restaurant = restaurant(
                LocalTime.of(11, 0), // restaurant open
                LocalTime.of(23, 0), // restaurant close
                null,                // deal open
                null,                // deal close
                null,                // deal start
                null                 // deal end
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getStart()).isEqualTo(LocalTime.of(11, 0));
    }

    @Test
    void shouldThrowException_When_RestaurantOpenIsNull() {
        // given
        var restaurant = restaurant(
                null,                 // restaurant open is null
                LocalTime.of(14, 0),  // restaurant close
                null,
                null,
                LocalTime.of(13, 0),  // deal start
                null
        );

        var deal = restaurant.deals().getFirst();

        // when + then
        assertThatThrownBy(() -> Deal.of(deal, restaurant))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid time window");
    }

    // deal end

    @Test
    void shouldReturn_DealEnd_When_WithinRestaurantHours() {
        // given
        var restaurant = restaurant(
                LocalTime.of(12, 0), // restaurant open
                LocalTime.of(15, 0), // restaurant close
                null,
                null,
                null,
                LocalTime.of(14, 0) // deal end (within restaurant hours)
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getEnd()).isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    void shouldReturn_RestaurantClose_When_EndIsOutsideRestaurantHours() {
        // given
        var restaurant = restaurant(
                LocalTime.of(12, 0), // restaurant open
                LocalTime.of(15, 0), // restaurant close
                null,
                null,
                null,
                LocalTime.of(17, 0) // deal end (after restaurant close)
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getEnd()).isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    void shouldReturn_RestaurantClose_When_DealOpenCloseAreDefined() {
        // given
        var restaurant = restaurant(
                LocalTime.of(10, 0), // restaurant open
                LocalTime.of(22, 0), // restaurant close
                LocalTime.of(12, 0), // deal open
                LocalTime.of(14, 0), // deal close (ignored)
                null,
                null
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getEnd()).isEqualTo(LocalTime.of(22, 0));
    }

    @Test
    void shouldReturn_RestaurantClose_When_NoTimeProvided() {
        // given
        var restaurant = restaurant(
                LocalTime.of(10, 0), // restaurant open
                LocalTime.of(22, 0), // restaurant close
                null,
                null,
                null,
                null
        );

        var deal = restaurant.deals().getFirst();

        // when
        Deal result = Deal.of(deal, restaurant);

        // then
        assertThat(result.getEnd()).isEqualTo(LocalTime.of(22, 0));
    }

    @Test
    void shouldThrowException_When_RestaurantCloseIsNull() {
        // given
        var restaurant = restaurant(
                LocalTime.of(12, 0), // restaurant open
                null,                // restaurant close is null
                null,
                null,
                null,
                LocalTime.of(14, 0)  // deal end
        );

        var deal = restaurant.deals().getFirst();

        // when + then
        assertThatThrownBy(() -> Deal.of(deal, restaurant))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid time window");
    }

    private RestaurantDto restaurant(LocalTime restaurantOpen, LocalTime restaurantClose,
                                     LocalTime dealOpen, LocalTime dealClose,
                                     LocalTime dealStart, LocalTime dealEnd

    ) {

        return new RestaurantDto(
                "objectId",
                "name",
                "address1",
                "suburb",
                List.of("cuisine1", "cuisine2"),
                "http://image.link",
                restaurantOpen,
                restaurantClose,
                List.of(
                        new DealDto(
                                "objectIdDeal1",
                                "discount",
                                "dineIn",
                                "lightning",
                                "qtyLeft",
                                dealOpen,
                                dealClose,
                                dealStart,
                                dealEnd
                        )
                )
        );
    }

}