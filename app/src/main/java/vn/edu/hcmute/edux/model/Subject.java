 package vn.edu.hcmute.edux.model;

import java.util.List;

public class Subject {
    private String classId;
    private String className;
    private String description;
    private List<StudyMaterial> studyMaterials;

    public Subject() {
    }

    // Constructor
    public Subject(String classId, String className, String description, List<StudyMaterial> studyMaterials) {
        this.classId = classId;
        this.className = className;
        this.description = description;
        this.studyMaterials = studyMaterials;
    }

    // Getters and Setters
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StudyMaterial> getStudyMaterials() {
        return studyMaterials;
    }

    public void setStudyMaterials(List<StudyMaterial> studyMaterials) {
        this.studyMaterials = studyMaterials;
    }
}
