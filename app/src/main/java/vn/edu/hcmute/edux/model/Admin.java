package vn.edu.hcmute.edux.model;

import java.util.Date;

public class Admin extends User {

    public Admin(String userID, String fullName, String email, String password, EUserRole role,
                 String imageUrl, Date birthDate, Boolean isActive, Date createdAt, Date updatedAt) {
        super(userID, fullName, email, password, role, imageUrl, birthDate, isActive, createdAt, updatedAt);
    }
}