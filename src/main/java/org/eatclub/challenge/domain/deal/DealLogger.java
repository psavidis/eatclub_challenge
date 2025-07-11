package org.eatclub.challenge.domain.deal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DealLogger {

    private static final Logger LOG = LoggerFactory.getLogger(DealLogger.class);

    public void logInvalidStartTime(String dealId, String fallbackValue) {
        String message = String.format(
                "{\"id\" : \"%s\", \"error\" : \"INVALID_START_TIME\", \"fallbackValue\" : \"%s\" }",
                dealId, fallbackValue);

        LOG.warn(message);
    }

    public void logInvalidEndTime(String dealId, String fallbackValue) {
        String message = String.format(
                "{\"id\" : \"%s\", \"error\" : \"INVALID_END_TIME\", \"fallbackValue\" : \"%s\" }",
                dealId, fallbackValue);

        LOG.warn(message);
    }
}