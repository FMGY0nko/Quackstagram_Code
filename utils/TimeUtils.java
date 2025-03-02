package utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getElapsedTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "Unknown";
        }
        try {
            LocalDateTime timeOfEvent = LocalDateTime.parse(timestamp, FORMATTER);
            LocalDateTime now = LocalDateTime.now();

            long daysBetween = ChronoUnit.DAYS.between(timeOfEvent, now);
            long minutesBetween = ChronoUnit.MINUTES.between(timeOfEvent, now) % 60;

            StringBuilder timeElapsed = new StringBuilder();
            if (daysBetween > 0) {
                timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
            }
            if (minutesBetween > 0) {
                if (daysBetween > 0) {
                    timeElapsed.append(" and ");
                }
                timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
            }
            return timeElapsed.toString();
        } catch (Exception e) {
            System.out.println("Error parsing timestamp: " + timestamp);
            return "Unknown";
        }
    }
}

