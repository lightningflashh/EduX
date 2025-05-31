package vn.edu.hcmute.edux.model;

import java.util.Date;

public class Notification {
    private String id; // Firebase document id
    private String title;
    private String content;
    private String classId;
    private Date timestamp;

    public Notification() {} // Required for Firebase

    public Notification(String id, String title, String content, String classId, Date timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.classId = classId;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getClassId() { return classId; }
    public Date getTimestamp() { return timestamp; }
}
