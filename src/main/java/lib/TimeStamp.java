package lib;

import java.time.Instant;

/**
 * A POJO representing a timestamp in millis. Provides methods to control whether the time stamp is
 * a UNIX or RFC868 timestamp.
 *
 * On reflection, I probably should have had two timestamp objects, one for Unix (or used Instant), one for RFC 868.
 */
public class TimeStamp {
    private static long SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH = 2208988800L;
    private long timeStamp;
    private Protocol protocol;

    private enum Protocol {
        UNIX,
        RFC868
    }

    private TimeStamp(long timeStamp, Protocol protocol) {
        this.timeStamp = timeStamp;
        this.protocol = protocol;
    }

    /**
     * Creates a UNIX TimeStamp from a RFC868 value
     */
    public static TimeStamp fromRFC868(long rfc868Timestamp) {
        return new TimeStamp(rfc868ToUnix(rfc868Timestamp), Protocol.UNIX);
    }

    /**
     * Creates a UNIX TimeStamp with the current time
     */
    public static TimeStamp unixNow() {
        return new TimeStamp(System.currentTimeMillis(), Protocol.UNIX);
    }

    /**
     * Creates a RFC868 TimeStamp with the current time
     */
    public static TimeStamp rfc868Now() {
        return new TimeStamp(unixToRFC868(System.currentTimeMillis()), Protocol.RFC868);
    }

    public int toInt() {
        return (int) timeStamp;
    }

    public long toLong() {
        return timeStamp;
    }

    /**
     * Transforms a UNIX TimeStamp into an java.time.Instant
     */
    public Instant toInstant() {
        if (!protocol.equals(Protocol.UNIX)) {
            throw new IllegalStateException("Can only be called with a UNIX TimeStamp");
        }
        return Instant.ofEpochMilli(timeStamp);
    }

    private static long unixToRFC868(long timeStamp) {
        return timeStamp / 1000L + SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH;
    }

    private static long rfc868ToUnix(long timeStamp) {
        return (timeStamp - SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH) * 1000L;
    }
}
