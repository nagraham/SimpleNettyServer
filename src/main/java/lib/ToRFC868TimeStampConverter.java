package lib;

public class ToRFC868TimeStampConverter implements TimeStampConverter {

    /**
     * Converts a epoch timestamp in millis to a RF868 value in seconds, which is the number of
     * seconds from Jan 01, 1900
     */
    @Override
    public Long convert(Long timeToConvert) {
        return timeToConvert / 1000L + TimeStampConverter.SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH;
    }
}
