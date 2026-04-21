package ftth.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a system user.
 * Maps to the 'users' table.
 */
public class User {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long userId;               // user_id (PK)
    private String username;           // username
    private String passwordHash;       // password_hash
    private Long roleId;               // role_id (FK)
    private boolean active;            // is_active
    private LocalDateTime createdAt;   // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public User() {
    }

    // Constructor for NEW user (before DB insert)
    public User(String username,
                String passwordHash,
                Long roleId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.active = true;
    }

    // Full constructor (used when reading from DB)
    public User(Long userId,
                String username,
                String passwordHash,
                Long roleId,
                boolean active,
                LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.active = active;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * IMPORTANT:
     * This stores the HASHED password, never the plain text password.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // ===============================
    // Domain helper methods
    // ===============================

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}