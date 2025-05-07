package vn.edu.hcmute.edux.model;

public class StudyMaterial {
    private String materialID;
    private String title;
    private String fileURL;
    private Classroom receiver;

    // Constructor
    public StudyMaterial(String materialID, String title, String fileURL, Classroom receiver) {
        this.materialID = materialID;
        this.title = title;
        this.fileURL = fileURL;
        this.receiver = receiver;
    }

    // Getters and Setters
    public String getMaterialID() { return materialID; }
    public void setMaterialID(String materialID) { this.materialID = materialID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileURL() { return fileURL; }
    public void setFileURL(String fileURL) { this.fileURL = fileURL; }

    public Classroom getReceiver() { return receiver; }
    public void setReceiver(Classroom receiver) { this.receiver = receiver; }
}