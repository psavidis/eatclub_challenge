package org.eatclub.challenge.assertions;

import org.assertj.core.api.Assertions;
import org.eatclub.challenge.ActiveDealsResponseDTO;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.fail;

public class DealAssertions {

    private final ResponseEntity<ActiveDealsResponseDTO> response;

    private DealAssertions(ResponseEntity<ActiveDealsResponseDTO>  response) {
        this.response = response;
    }

    public static DealAssertions assertThat(ResponseEntity<ActiveDealsResponseDTO> response) {
        return new DealAssertions(response);
    }

    public DealAssertions hasStatusCode(int expectedStatusCode) {
        if (response.getStatusCodeValue() != expectedStatusCode) {
            fail("Expected status code " + expectedStatusCode
                    + " but got " + response.getStatusCodeValue());
        }

        return this;
    }

    public DealAssertions hasNonNullResponseBody() {
        Assertions.assertThat(response.getBody()).isNotNull();

        return this;
    }

    public DealAssertions allDealsHaveOpenRestaurantAtTimeOfDay(String timeOfDay) {
        var body = response.getBody();
        var deals = body.deals();

        deals.forEach(d -> {
            DealAssertion.assertThat(d).hasOpenRestaurantAtTimeOfDay(timeOfDay);
        });

        return this;
    }

}
