package org.eatclub.challenge.common;

import java.time.LocalTime;

/**
 * Class modeling a local time window defined by a start and end LocalTime.
 */
public class LocalTimeWindow {

    private final LocalTime start;
    private final LocalTime end;

    private LocalTimeWindow(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Static factory method to create a LocalTimeWindow instance
     * @throws IllegalArgumentException in case of invalid time window dates
     */
    public static LocalTimeWindow of(LocalTime start, LocalTime end) {
        if (!isValid(start, end)) {
            throw new IllegalArgumentException("Invalid time window (" + start + ", " + end + "): "
                    + "start must be before or equal to end, and neither can be null.");
        }

        return new LocalTimeWindow(start, end);
    }

    public static boolean isValidWindow(LocalTime start, LocalTime end) {
        try {
            return isValid(start, end);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the given time falls within the window
     *
     * @param time the given time to check
     * @return true if the time is within the window, false otherwise
     */
    public boolean contains(LocalTime time) {
        return !time.isBefore(start) && !time.isAfter(end);
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    private static boolean isValid(LocalTime start, LocalTime end) {
        return start != null && end != null && !start.isAfter(end);
    }

}
