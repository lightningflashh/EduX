package vn.edu.hcmute.edux.model;

public class Classroom {
    private String classID;
    private String className;
    private String description;

    // Constructor
    public Classroom(String classID, String className, String description) {
        this.classID = classID;
        this.className = className;
        this.description = description;
    }

    // Getters and Setters
    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}