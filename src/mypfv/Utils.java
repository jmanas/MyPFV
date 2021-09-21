package mypfv;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.*;

public class Utils {
    public static long getMins(Image1 img1, Image1 img2) {
        if (img1 == null || img2 == null)
            return 1;
        return MINUTES.between(img1.getDate(), img2.getDate());
    }

    public static long getHours(Image1 img1, Image1 img2) {
        if (img1 == null || img2 == null)
            return 1;
        return HOURS.between(img1.getDate(), img2.getDate());
    }

    public static long getDays(Image1 img1, Image1 img2) {
        if (img1 == null || img2 == null)
            return 1;
        return DAYS.between(img1.getDate(), img2.getDate());
    }

    public static long getWeeks(Image1 img1, Image1 img2) {
        if (img1 == null || img2 == null)
            return 1;
        return WEEKS.between(img1.getDate(), img2.getDate());
    }
}
