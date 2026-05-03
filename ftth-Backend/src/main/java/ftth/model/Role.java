package ftth.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a system role.
 * Maps to the 'roles' table.
 */
public class Role {

    // ===============================
    // Fields (DB columns)
    // ===============================

    private Long roleId;               // role_id (PK)
    private String roleCode;           // role_code (e.g. ADMIN, CSR, USER)
    private LocalDateTime createdAt;   // created_at


    // ===============================
    // Constructors
    // ===============================

    // No-args constructor (required by frameworks)
    public Role() {
    }

    // Constructor for NEW role (before DB insert)
    public Role(String roleCode) {
        this.roleCode = roleCode;
    }

    // Full constructor (used when reading from DB)
    public Role(Long roleId,
                String roleCode,
                LocalDateTime createdAt) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.createdAt = createdAt;
    }


    // ===============================
    // Getters and Setters
    // ===============================

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.roleCode);
    }

    public boolean isCsr() {
        return "CSR".equalsIgnoreCase(this.roleCode);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(this.roleCode);
    }


    // ===============================
    // toString (debugging)
    // ===============================

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleCode='" + roleCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}