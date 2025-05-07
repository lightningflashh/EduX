package vn.edu.hcmute.edux.model;

import java.util.Date;
import java.util.List;

public class Student extends User {
    private String studentCode;
    private String major;
    private String studyTime;
    private List<Subject> joinedClasses;

    // Constructor

    public Student() {
    }

    public Student(String userID, String fullName, String email, String password, EUserRole role,
                   String imageUrl, Date birthDate, Boolean isActive, Date createdAt, Date updatedAt,
                   String studentCode, String major,String studyTime, List<Subject> joinedClasses) {
        super(userID, fullName, email, password, role, imageUrl, birthDate, isActive, createdAt, updatedAt);
        this.studentCode = studentCode;
        this.major = major;
        this.studyTime = studyTime;
        this.joinedClasses = joinedClasses;
    }

    // Getters and Setters
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }


    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getStudyTime() { return studyTime; }
    public void setStudyTime(String studyTime) { this.studyTime = studyTime; }


    public List<Subject> getJoinedClasses() {
        return joinedClasses;
    }
    public void setJoinedClasses(List<Subject> joinedClasses) {
        this.joinedClasses = joinedClasses;
    }


}