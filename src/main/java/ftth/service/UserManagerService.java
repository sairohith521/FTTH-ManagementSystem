package ftth.service;
import java.io.*;

/**
 * UserManager — handles login, role lookup, and user CRUD.
 *
 * users.txt format (one line per user):
 *   username,password,role
 *
 * Roles  : ADMIN | CSR | MAINT
 * Default seed written on first run if file is missing.
 */
public class UserManagerService {

    private static final String USERS_FILE = "users.txt";

    // =========================================================
    // BOOT — create default users.txt if it does not exist
    // =========================================================
    public static void initDefaults() {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            try (FileWriter fw = new FileWriter(f)) {
                fw.write("admin,admin123,ADMIN\n");
                fw.write("maint,maint123,MAINT\n");
                fw.write("csr,csr123,CSR\n");
            } catch (Exception e) {
                System.out.println("Could not create users.txt: " + e.getMessage());
            }
        }
    }

    // =========================================================
    // LOGIN — returns role string or null on failure
    // =========================================================
    public String login(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 3) continue;
                if (p[0].trim().equals(username) && p[1].trim().equals(password)) {
                    return p[2].trim().toUpperCase();
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
        }
        return null;   // invalid credentials
    }

    // =========================================================
    // LIST all users (for admin view)
    // =========================================================
    public void listUsers() {
        System.out.println("\n--- Registered Users ---");
        System.out.printf("%-20s %-20s %-10s%n", "Username", "Password", "Role");
        System.out.println("-".repeat(52));
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 3) continue;
                System.out.printf("%-20s %-20s %-10s%n", p[0].trim(), p[1].trim(), p[2].trim());
                found = true;
            }
            if (!found) System.out.println("  (no users found)");
        } catch (Exception e) {
            System.out.println("Error listing users: " + e.getMessage());
        }
        System.out.println();
    }
    // =========================================================
    // ADD a new user
    // =========================================================
    public boolean addUser(String username, String password, String role) {
        username = username.trim();
        password = password.trim();
        role     = role.trim().toUpperCase();

        // Validate role
        if (!role.equals("CSR") && !role.equals("MAINT")) {
            System.out.println("Invalid role. Must be  CSR, or MAINT.");
            return false;
        }

        // Check duplicate
        if (userExists(username)) {
            System.out.println("Username '" + username + "' already exists.");
            return false;
        }

        try (FileWriter fw = new FileWriter(USERS_FILE, true)) {
            fw.write(username + "," + password + "," + role + "\n");
            System.out.println(" User '" + username + "' added with role " + role + ".");
            return true;
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================
    public boolean changePassword(String username, String newPassword) {
        return rewriteUser(username, newPassword, null);
    }

    // =========================================================
    // CHANGE ROLE
    // =========================================================
    public boolean changeRole(String username, String newRole) {
        newRole = newRole.trim().toUpperCase();
        if ( !newRole.equals("CSR") && !newRole.equals("MAINT")) {
            System.out.println("Invalid role. Must be  CSR, or MAINT.");
            return false;
        }
        return rewriteUser(username, null, newRole);
    }

    // =========================================================
    // DELETE a user
    // =========================================================
    public boolean deleteUser(String username) {
        username = username.trim();
        if (!userExists(username)) {
            System.out.println("User '" + username + "' not found.");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        boolean removed  = false;

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] p = line.split(",");
                if (p[0].trim().equals(username)) {
                    removed = true;   // skip this line → user deleted
                } else {
                    sb.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }

        if (!removed) return false;

        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            fw.write(sb.toString());
            System.out.println(" User '" + username + "' deleted.");
            return true;
        } catch (Exception e) {
            System.out.println("Error saving users.txt: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p[0].trim().equals(username)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** Generic rewriter — pass null to keep existing value. */
    private boolean rewriteUser(String username, String newPassword, String newRole) {
        username = username.trim();
        if (!userExists(username)) {
            System.out.println("User '" + username + "' not found.");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        boolean updated  = false;

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    sb.append(line).append("\n");
                    continue;
                }
                String[] p = line.split(",");
                if (p[0].trim().equals(username)) {
                    if (newPassword != null) p[1] = newPassword;
                    if (newRole     != null) p[2] = newRole;
                    updated = true;
                }
                sb.append(p[0]+","+p[1]+","+p[2]+"\n");
            }
        } catch (Exception e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
            return false;
        }

        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            fw.write(sb.toString());
            return updated;
        } catch (Exception e) {
            System.out.println("Error saving users.txt: " + e.getMessage());
            return false;
        }
    }
}
