package vn.edu.hcmute.edux.model;

import java.util.Date;

public abstract class User {
    private String userID;
    private String fullName;
    private String email;
    private String password;
    private EUserRole role;
    private String imageUrl;
    private Date birthDate;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    // Constructor
    public User(String userID, String fullName, String email, String password, EUserRole role,
                String imageUrl, Date birthDate, Boolean isActive, Date createdAt, Date updatedAt) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imageUrl = imageUrl;
        this.birthDate = birthDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {
    }

    // Getters and Setters
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public EUserRole getRole() { return role; }
    public void setRole(EUserRole role) { this.role = role; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

}