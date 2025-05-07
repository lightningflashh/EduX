package vn.edu.hcmute.edux.model;

public class Notification {
    private int id;
    private String title;
    private String content;

    public Notification(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
}