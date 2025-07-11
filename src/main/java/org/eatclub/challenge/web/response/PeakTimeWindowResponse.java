package org.eatclub.challenge.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record PeakTimeWindowResponse(
        @JsonFormat(pattern = "HH:mm") LocalTime peakTimeStart,
        @JsonFormat(pattern = "HH:mm") LocalTime peakTimeEnd
) {}