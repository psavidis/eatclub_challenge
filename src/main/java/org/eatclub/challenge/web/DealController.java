package org.eatclub.challenge.web;

import org.eatclub.challenge.domain.deal.DealService;
import org.eatclub.challenge.domain.restaurant.RestaurantService;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;
import org.eatclub.challenge.web.response.ActiveDealsResponse;
import org.eatclub.challenge.web.response.PeakTimeWindowResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;
    private final RestaurantService restaurantService;

    public DealController(DealService dealService, RestaurantService restaurantService) {
        this.dealService = dealService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/active")
    public ActiveDealsResponse getActiveDeals(@RequestParam(name = "timeOfDay") String timeOfDay) {
        return dealService.getActiveDeals(timeOfDay);
    }

    @GetMapping("/peak")
    public PeakTimeWindowResponse getPeakWindow() {
        List<RestaurantDto> allRestaurants = restaurantService.getRestaurants();
        return dealService.calculatePeakWindow(allRestaurants);
    }

}