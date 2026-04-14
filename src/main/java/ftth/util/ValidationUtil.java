package ftth.util;

public class ValidationUtil {

    public static boolean isValidPincode(String pincode) {

        if (pincode == null) return false;

        // Must be exactly 6 digits
        if (!pincode.matches("^[1-9][0-9]{5}$")) {
            return false;
        }

        return true;
    }
}
