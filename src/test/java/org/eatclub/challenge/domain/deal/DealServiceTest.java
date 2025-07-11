package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.eatclub.challenge.web.response.PeakTimeWindowResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DealServiceTest {

    private final DealService dealService = new TestDealService();

    @Test
    void shouldReturnNullPeakWindowOnEmptyDeals() {
        // given a restaurant with no deals
        List<RestaurantDto> restaurants = List.of(restaurant("08:00", "22:00", Collections.emptyList()));

        // when
        PeakTimeWindowResponse peak = dealService.calculatePeakWindow(restaurants);

        // then the window is null
        assertThat(peak.peakTimeStart()).isNull();
        assertThat(peak.peakTimeEnd()).isNull();
    }

    @Test
    void shouldIgnoreDealsWithNullStartOrEnd() {
        List<RestaurantDto> restaurants = List.of(
                restaurant("08:00", "22:00", List.of(
                        deal(null, "10:00"),
                        deal("11:00", null),
                        deal(null, null),
                        deal("09:00", "10:00")
                ))
        );

        PeakTimeWindowResponse peak = dealService.calculatePeakWindow(restaurants);

        // then the peak window is calculated correctly
        assertThat(peak.peakTimeStart()).isEqualTo(LocalTime.of(9, 0));
        assertThat(peak.peakTimeEnd()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void shouldReturnCorrectPeakWindow() {
        // given
        List<RestaurantDto> restaurants = List.of(
                restaurant("08:00", "22:00", List.of(
                        deal("10:00", "11:00"),
                        deal("10:30", "11:30"),
                        deal("10:45", "11:15")
                )),
                restaurant("08:00", "22:00", List.of(
                        deal("18:00", "19:00")
                ))
        );

        // when
        PeakTimeWindowResponse peak = dealService.calculatePeakWindow(restaurants);

        // then
        assertThat(peak.peakTimeStart()).isEqualTo(LocalTime.of(10, 45));
        assertThat(peak.peakTimeEnd()).isEqualTo(LocalTime.of(11, 0));
    }

    @Test
    void shouldPickFirstPeakOnTwoPeaksWithSameCount() {
        // given two peaks with the same count
        List<RestaurantDto> restaurants = List.of(
                restaurant("08:00", "22:00", List.of(
                        deal("10:00", "10:05"), // first peak
                        deal("10:01", "10:06"),
                        deal("20:00", "20:05"), // second peak
                        deal("20:01", "20:06")
                ))
        );

        // when
        PeakTimeWindowResponse peak = dealService.calculatePeakWindow(restaurants);

        // then the first peak found is returned
        assertThat(peak.peakTimeStart()).isEqualTo(LocalTime.of(10, 1));
        assertThat(peak.peakTimeEnd()).isEqualTo(LocalTime.of(10, 5));
    }

    private static RestaurantDto restaurant(String open, String close, List<DealDto> deals) {
        return new RestaurantDto(
                "id",
                "Test Restaurant",
                "Test Address",
                "Test Suburb",
                List.of("Test Cuisine"),
                null,
                LocalTime.parse(open),
                LocalTime.parse(close),
                deals
        );
    }

    private static DealDto deal(String start, String end) {
        return new DealDto(
                "deal-id",
                "50",
                "true",
                "false",
                "10",
                null,
                null,
                start != null ? LocalTime.parse(start) : null,
                end != null ? LocalTime.parse(end) : null
        );
    }

    static class TestDealService extends DealService {
        public TestDealService() {
            super(null);
        }
    }

}