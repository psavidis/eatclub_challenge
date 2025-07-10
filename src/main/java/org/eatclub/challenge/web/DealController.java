package org.eatclub.challenge.web;

import org.eatclub.challenge.domain.deal.DealService;
import org.eatclub.challenge.web.response.ActiveDealsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping("/active")
    public ActiveDealsResponse getActiveDeals(@RequestParam(name = "timeOfDay") String timeOfDay) {
        return dealService.getActiveDeals(timeOfDay);
    }

}