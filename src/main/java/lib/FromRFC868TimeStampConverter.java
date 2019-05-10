package lib;

public class FromRFC868TimeStampConverter implements TimeStampConverter {

    /**
     * Converts a RF868 value in seconds, which is the number of seconds from Jan 01, 1900,
     * to a epoch timestamp in millis.
     */
    @Override
    public Long convert(Long timeToConvert) {
        return (timeToConvert - TimeStampConverter.SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH) * 1000L;
    }
}
