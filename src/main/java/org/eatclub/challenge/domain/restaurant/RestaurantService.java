package org.eatclub.challenge.domain.restaurant;

import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
public class RestaurantService {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurantService.class);

    private static final String URL = "https://eccdn.com.au/misc/challengedata.json";

    private static final long CACHE_EVICT_SECONDS = 300;

    private final RestTemplate restTemplate;

    public RestaurantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the list of restaurants from the external API.
     * Caches the result for 5 minutes.
     * @return
     */
    @Cacheable("restaurants")
    public List<RestaurantDto> getRestaurants() {
        LOG.debug("Fetching restaurants from external API at {}", LocalTime.now());

        ResponseEntity<RestaurantResponse> response = restTemplate.getForEntity(URL, RestaurantResponse.class);
        Optional<RestaurantResponse> result  = Optional.ofNullable(response.getBody());

        return result.map(RestaurantResponse::restaurants)
                .orElse(Collections.emptyList());
    }

    @Scheduled(fixedRate = CACHE_EVICT_SECONDS * 1000)
    @CacheEvict(value = "restaurants", allEntries = true)
    public void evictRestaurantsCache() {
        LOG.debug("Evicted restaurant cache at {}", LocalTime.now());
    }

}