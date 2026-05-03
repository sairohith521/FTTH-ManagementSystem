package ftth.util;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputUtil {

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                sc.nextLine(); // consume newline
                return val;
            } else {
                System.out.println("  Please enter numbers only.");
                sc.nextLine(); // clear invalid input
            }
        }
    }
    public static String readOLTType(Scanner sc) {

    while (true) {
        System.out.println("Select OLT Type:");
        System.out.println("  [1] OLT300");
        System.out.println("  [2] OLT500");
        System.out.print("Choice: ");

        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                return "OLT300";
            case "2":
                return "OLT500";
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }
}

    public static String readString(Scanner sc, String prompt) {
    while (true) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();

        if (!input.isEmpty()) {
            return input.toUpperCase(); // ✅ customer codes usually uppercase
        }

        System.out.println("Input cannot be empty. Please try again.");
    }
}


    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double val = sc.nextDouble();
                sc.nextLine(); // consume newline
                return val;
            } else {
                System.out.println("  Please enter a valid number.");
                sc.nextLine(); // clear invalid input
            }
        }
    }
    public static String readValidName(Scanner sc, String prompt) {

    while (true) {
        System.out.print(prompt);
        String name = sc.nextLine().trim();

        // 🔹 Validation: only letters + spaces, min 2 chars
        if (name.matches("[a-zA-Z ]{2,}") && name.length()<20) {
            return name;
        } else {
            System.out.println("[ERROR] Invalid name. Only letters allowed (min 2 chars).");
        }
    }
}
public static String readValidUsername(Scanner sc, String prompt) {

    while (true) {
        System.out.print(prompt);
        String username = sc.nextLine().trim();

        // 🔹 Rules:
        // ✔ letters + numbers + underscore
        // ✔ length 3–15
        if (username.matches("[a-zA-Z0-9_]{3,15}")) {
            return username;
        } else {
            System.out.println("[ERROR] Invalid username. Use 3-15 chars (letters, numbers, _ only).");
        }
    }
}
public static String readPassword(String prompt) {

    if (System.console() != null) {
        // 🔥 Real masking (no characters shown)
        char[] passwordArray = System.console().readPassword(prompt);
        return new String(passwordArray);
    } else {
        // ⚠ fallback (VS Code / IDE issue)
        System.out.print(prompt);
        return new java.util.Scanner(System.in).nextLine();
    }
}
public static String readMenuOption(Scanner sc, String prompt) {

    while (true) {
        System.out.print(prompt);
        String option = sc.nextLine().trim().toUpperCase();

        // 🔹 Allow 1–9 OR A
        if (option.matches("[0-9A]")) {
            return option;
        } else {
            System.out.println("❌ Invalid option. Enter 1-9 or A only.");
        }
    }
}
public static String readEmail(Scanner sc, String prompt) {
    while (true) {
        System.out.print(prompt);
        String email = sc.nextLine().trim();

        // ✅ Simple email regex
        if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return email;
        } else {
            System.out.println("[ERROR] Invalid email format. Try again.");
        }
    }
}
public static long readLong(Scanner sc, String prompt) {
    while (true) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number. Please enter a valid numeric value.");
        }
    }
}
 // =========================
    // Plan Name
    // =========================
    public static String readPlanName(Scanner sc) {
        while (true) {
            System.out.print("Enter Plan Name: ");
            String name = sc.nextLine().trim();
            if (ValidationUtil.isValidPlanName(name)) {
                return name;
            }
            System.out.println(
                "Invalid name. Only letters, numbers and spaces allowed (2-50 chars)."
            );
        }
    }

    // =========================
    // Speed
    // =========================
    public static String readSpeed(Scanner sc) {
        while (true) {
            System.out.print("Enter Speed (MBPS): ");
            String val = sc.nextLine().trim();
            if (ValidationUtil.isValidSpeed(val)) {
                return val + "MBPS";
            }
            System.out.println(
                "Invalid speed. Enter a number only (e.g. 300, 500, 1000)."
            );
        }
    }

    // =========================
    // Data Limit
    // =========================
    public static String readDataLimit(Scanner sc) {
        while (true) {
            System.out.print("Enter Data Limit (GB) or 'Unlimited': ");
            String val = sc.nextLine().trim();
            if (ValidationUtil.isValidDataLimit(val)) {
                if (val.equalsIgnoreCase("unlimited")) {
                    return "Unlimited";
                }
                return val + "GB";
            }
            System.out.println(
                "Invalid input. Enter a number (e.g. 60) or 'Unlimited'."
            );
        }
    }

    // =========================
    // OLT Type
    // =========================
    public static String readOltType(Scanner sc) {
        while (true) {
            System.out.print("Enter OLT Type [OLT300/OLT500]: ");
            String olt = sc.nextLine().trim().toUpperCase();
            if (ValidationUtil.isValidOltType(olt)) {
                return olt;
            }
            System.out.println(
                "Invalid OLT type. Enter OLT300 or OLT500."
            );
        }
    }

    // =========================
    // Integer input
    // =========================
    public static int readInt(Scanner sc) {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    // =========================
    // Long input
    // =========================
    public static long readLong(Scanner sc) {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid id: ");
            }
        }
    }

    // =========================
    // BigDecimal input
    // =========================
    public static BigDecimal readBigDecimal(Scanner sc) {
        while (true) {
            String value = sc.nextLine().trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid amount: ");
            }
        }
    }
}








   