package lib;

@FunctionalInterface
public interface TimeStampConverter {
    long SECONDS_FROM_01_JAN_1900_TO_UNIX_EPOCH = 2208988800L;
    Long convert(Long aLong);
}
