package ftth.util;

public class ValidationUtil {

    public static boolean isValidPincode(String pincode) {
        if (pincode == null) return false;
        return pincode.matches("^[1-9][0-9]{5}$");
    }

    public static boolean isValidPlanName(String name) {
        if (name == null || name.isBlank()) return false;
        return name.matches("[a-zA-Z0-9 ]{2,50}");
    }

    public static boolean isValidSpeed(String speed) {
        if (speed == null) return false;
        try {
            int val = Integer.parseInt(speed.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDataLimit(String data) {
        if (data == null || data.isBlank()) return false;
        if (data.equalsIgnoreCase("unlimited")) return true;
        try {
            int val = Integer.parseInt(data.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidOltType(String olt) {
        if (olt == null) return false;
        return "OLT300".equals(olt.trim().toUpperCase()) || "OLT500".equals(olt.trim().toUpperCase());
    }
}
