package ftth.model;

import java.sql.Timestamp;

public class Role {

    private long roleId;
    private String roleCode;
    private Timestamp createdAt;

    // 🔹 Default Constructor
    public Role() {
    }

    // 🔹 Parameterized Constructor
    public Role(long roleId, String roleCode, Timestamp createdAt) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.createdAt = createdAt;
    }

    // 🔹 Getters and Setters

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 🔹 toString()

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleCode='" + roleCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}