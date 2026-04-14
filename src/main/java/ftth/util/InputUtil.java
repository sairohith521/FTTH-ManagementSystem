package ftth.util;

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
}