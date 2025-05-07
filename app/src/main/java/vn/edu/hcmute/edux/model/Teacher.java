package vn.edu.hcmute.edux.model;

import java.util.Date;
import java.util.List;



public class Teacher extends User {
    private String degree;
    private List<Subject> teachingClasses;
    private String major;

    // Constructor
    public Teacher(String userID, String fullName, String email, String password, EUserRole role,
                   String imageUrl, Date birthDate, Boolean isActive, Date createdAt, Date updatedAt,
                   String degree, List<Subject> teachingClasses, String major) {
        super(userID, fullName, email, password, role, imageUrl, birthDate, isActive, createdAt, updatedAt);
        this.degree = degree;
        this.teachingClasses = teachingClasses;
        this.major = major;
    }

    // Default constructor for Firestore
    public Teacher() {
        super();
    }

    // Getters and Setters
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<Subject> getTeachingClasses() {
        return teachingClasses;
    }

    public void setTeachingClasses(List<Subject> teachingClasses) {
        this.teachingClasses = teachingClasses;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
