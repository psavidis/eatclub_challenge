package org.eatclub.challenge.domain.deal;

import org.eatclub.challenge.domain.restaurant.dto.DealDto;
import org.eatclub.challenge.domain.restaurant.dto.RestaurantDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the details of a deal, including information about the restaurant and the deal itself.
 */
public class DealDetails {

    private static final String RESTAURANT_OBJECT_ID = "restaurantObjectId";
    private static final String RESTAURANT_NAME = "restaurantName";
    private static final String RESTAURANT_ADDRESS1 = "restaurantAddress1";
    private static final String RESTAURANT_SUBURB = "restaurantSuburb";
    private static final String RESTAURANT_OPEN = "restaurantOpen";
    private static final String RESTAURANT_CLOSE = "restaurantClose";

    private static final String DEAL_OBJECT_ID = "dealObjectId";

    private static final String DISCOUNT = "discount";
    private static final String DINE_IN = "dineIn";
    private static final String LIGHTING = "lightning";
    private static final String QTY_LEFT = "qtyLeft";

    private final Map<String, String> properties;

    private DealDetails(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Static factory method to create a DealDetails instance from DealDto and RestaurantDto.
     */
    public static DealDetails of(DealDto dealDto, RestaurantDto restaurantDto) {
        Map<String, String> properties = new HashMap<>();

        properties.put(RESTAURANT_OBJECT_ID, restaurantDto.objectId());
        properties.put(RESTAURANT_NAME, restaurantDto.name());
        properties.put(RESTAURANT_ADDRESS1, restaurantDto.address1());
        properties.put(RESTAURANT_SUBURB, restaurantDto.suburb());
        properties.put(RESTAURANT_OPEN, restaurantDto.open().toString());
        properties.put(RESTAURANT_CLOSE, restaurantDto.close().toString());
        properties.put(DEAL_OBJECT_ID, dealDto.objectId());
        properties.put(DISCOUNT, dealDto.discount());
        properties.put(DINE_IN, dealDto.dineIn());
        properties.put(LIGHTING, dealDto.lightning());
        properties.put(QTY_LEFT, String.valueOf(dealDto.qtyLeft()));

        return new DealDetails(properties);
    }

    private String getProperty(String key) {
        return properties.get(key);
    }

    public String getRestaurantObjectId() {
        return getProperty(RESTAURANT_OBJECT_ID);
    }

    public String getRestaurantName() {
        return getProperty(RESTAURANT_NAME);
    }

    public String getRestaurantAddress1() {
        return getProperty(RESTAURANT_ADDRESS1);
    }

    public String getRestaurantSuburb() {
        return getProperty(RESTAURANT_SUBURB);
    }

    public String getRestaurantOpen() {
        return getProperty(RESTAURANT_OPEN);
    }

    public String getRestaurantClose() {
        return getProperty(RESTAURANT_CLOSE);
    }

    public String getDealObjectId() {
        return getProperty(DEAL_OBJECT_ID);
    }

    public String getDiscount() {
        return getProperty(DISCOUNT);
    }

    public String getDineIn() {
        return getProperty(DINE_IN);
    }

    public String getLightning() {
        return getProperty(LIGHTING);
    }

    public String getQtyLeft() {
        return getProperty(QTY_LEFT);
    }
}
