package vn.edu.hcmute.edux.model;

import java.util.Date;
import java.util.List;

public class Quiz {
    private String quizID;
    private String title;
    private String description;
    private String createdBy;
    private String classID;
    private Date timeStart;
    private Date timeEnd;
    private long duration;
    private EQuizStatus status;
    private List<Question> questions;
    public Quiz() {}

    // Constructor
    public Quiz(String quizID, String title, String description, String createdBy,
                String classID, Date timeStart, Date timeEnd, long duration, EQuizStatus status, List<Question> questions) {
        this.quizID = quizID;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.classID = classID;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.status = status;
        this.duration = duration;
        this.questions = questions;
    }

    // Getters and Setters
    public String getQuizID() { return quizID; }
    public void setQuizID(String quizID) { this.quizID = quizID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }

    public Date getTimeStart() { return timeStart; }
    public void setTimeStart(Date timeStart) { this.timeStart = timeStart; }

    public Date getTimeEnd() { return timeEnd; }
    public void setTimeEnd(Date timeEnd) { this.timeEnd = timeEnd; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public EQuizStatus getStatus() { return status; }
    public void setStatus(EQuizStatus status) { this.status = status; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

}