package vn.edu.hcmute.edux.model;

public class StudyMaterial {
    private String materialID;
    private String title;
    private String fileURL;
    private String classId;
    private String teacherId;
    private EDocumentType type;

    private long uploadedAt;
    // Constructor
    public StudyMaterial() {}

    public StudyMaterial(String id, String title, String url, String classId, String teacherId, EDocumentType type, long uploadedAt) {
        this.materialID = id;
        this.title = title;
        this.fileURL = url;
        this.classId = classId;
        this.teacherId = teacherId;
        this.type = type;
        this.uploadedAt = uploadedAt;
    }

    public String getId() { return materialID; }
    public String getTitle() { return title; }
    public String getUrl() { return fileURL; }
    public String getClassId() { return classId; }
    public String getTeacherId() { return teacherId; }
    public EDocumentType getType() { return type; }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }


}