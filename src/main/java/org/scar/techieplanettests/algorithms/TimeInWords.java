package org.scar.techieplanettests.algorithms;

/**
 * Programming &amp; Algorithm — Question 1.
 *
 * <p>Converts a time given in numerals (H, M) into English words, e.g. {@code 5:47}
 * becomes {@code "thirteen minutes to six"}.
 *
 * <p>Rules applied (matching the examples in the assessment):
 * <ul>
 *   <li>M = 0  → "&lt;hour&gt; o'clock"</li>
 *   <li>M = 15 → "quarter past &lt;hour&gt;"</li>
 *   <li>M = 30 → "half past &lt;hour&gt;"</li>
 *   <li>M = 45 → "quarter to &lt;next hour&gt;"</li>
 *   <li>M &lt; 30 → "&lt;M&gt; minute(s) past &lt;hour&gt;"</li>
 *   <li>M &gt; 30 → "&lt;60 - M&gt; minute(s) to &lt;next hour&gt;"</li>
 * </ul>
 */
public final class TimeInWords {

    static final int MIN_HOUR = 1;
    static final int MAX_HOUR = 12;
    static final int MIN_MINUTE = 0;
    static final int MAX_MINUTE = 59;

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HALF_HOUR = 30;
    private static final int QUARTER_HOUR = 15;
    private static final int THREE_QUARTER_HOUR = 45;

    private static final String[] ONES = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
    };

    private TimeInWords() {
    }

    /**
     * Converts the given time to lowercase English words.
     *
     * @param hour   the hour, {@code 1 <= hour <= 12}
     * @param minute the minute, {@code 0 <= minute <= 59}
     * @return the time in words, e.g. {@code "thirteen minutes to six"}
     * @throws IllegalArgumentException if the hour or minute is out of range
     */
    public static String toWords(int hour, int minute) {
        validate(hour, minute);

        return switch (minute) {
            case 0                  -> hourInWords(hour) + " o'clock";
            case QUARTER_HOUR       -> "quarter past " + hourInWords(hour);
            case HALF_HOUR          -> "half past " + hourInWords(hour);
            case THREE_QUARTER_HOUR -> "quarter to " + hourInWords(nextHour(hour));
            default                 -> minute < HALF_HOUR
                    ? minutesInWords(minute) + " past " + hourInWords(hour)
                    : minutesInWords(MINUTES_IN_HOUR - minute) + " to " + hourInWords(nextHour(hour));
        };
    }

    /**
     * Same as {@link #toWords(int, int)} but with the first letter capitalised,
     * matching the sample output {@code "Thirteen minutes to six"}.
     */
    public static String toSentence(int hour, int minute) {
        String words = toWords(hour, minute);
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    private static void validate(int hour, int minute) {
        if (hour < MIN_HOUR || hour > MAX_HOUR) {
            throw new IllegalArgumentException(
                    "Hour must be between " + MIN_HOUR + " and " + MAX_HOUR + " but was " + hour);
        }
        if (minute < MIN_MINUTE || minute > MAX_MINUTE) {
            throw new IllegalArgumentException(
                    "Minute must be between " + MIN_MINUTE + " and " + MAX_MINUTE + " but was " + minute);
        }
    }

    private static int nextHour(int hour) {
        return hour == MAX_HOUR ? MIN_HOUR : hour + 1;
    }

    private static String hourInWords(int hour) {
        return ONES[hour];
    }

    private static String minutesInWords(int minutes) {
        String number = numberInWords(minutes);
        return minutes == 1 ? number + " minute" : number + " minutes";
    }

    private static String numberInWords(int value) {
        if (value < 20) {
            return ONES[value];
        }
        int remainder = value - 20;
        return remainder == 0 ? "twenty" : "twenty-" + ONES[remainder];
    }
}
