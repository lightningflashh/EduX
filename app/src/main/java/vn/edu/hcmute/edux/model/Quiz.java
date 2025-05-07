package vn.edu.hcmute.edux.model;

import java.util.Date;
import java.util.List;

public class Quiz {
    private String quizID;
    private String name;
    private Date timeStart;
    private Date timeEnd;
    private EQuizStatus status;
    private List<Question> questions;

    // Constructor
    public Quiz(String quizID, String name, Date timeStart, Date timeEnd, EQuizStatus status, List<Question> questions) {
        this.quizID = quizID;
        this.name = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.status = status;
        this.questions = questions;
    }

    // Getters and Setters
    public String getQuizID() { return quizID; }
    public void setQuizID(String quizID) { this.quizID = quizID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getTimeStart() { return timeStart; }
    public void setTimeStart(Date timeStart) { this.timeStart = timeStart; }

    public Date getTimeEnd() { return timeEnd; }
    public void setTimeEnd(Date timeEnd) { this.timeEnd = timeEnd; }

    public EQuizStatus getStatus() { return status; }
    public void setStatus(EQuizStatus status) { this.status = status; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}