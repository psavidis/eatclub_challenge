package org.eatclub.challenge.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eatclub.challenge.jackson.CaseInsensitiveLocalTimeDeserializer;

import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DealDto(
        String objectId,
        String discount,
        String dineIn,
        String lightning,
        String qtyLeft,

        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime open,
        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime close,
        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime start,
        @JsonDeserialize(using = CaseInsensitiveLocalTimeDeserializer.class)
        LocalTime end
) {
}
