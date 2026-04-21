package ftth.util;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Utility class for bill-related helper functions.
 */
public final class BillUtil {

    // ===============================
    // Configuration
    // ===============================

    private static final String PREFIX = "BILL";
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // Prevent instantiation
    private BillUtil() {
    }

    /**
     * Generate a unique bill number.
     *
     * Example: BILL-2026-000001
     */
    public static String generateBillNo() {

        int year = LocalDate.now().getYear();
        long seq = SEQUENCE.getAndIncrement();

        return String.format(
            "%s-%d-%06d",
            PREFIX,
            year,
            seq
        );
    }
}