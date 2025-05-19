package vn.edu.hcmute.edux.model;

import java.util.List;

public class Question {
    private String questionID;
    private String content;
    private List<Answer> answers;

    // Default constructor
    public Question() {
    }

    // Constructor
    public Question(String questionID, String content, List<Answer> answers) {
        this.questionID = questionID;
        this.content = content;
        this.answers = answers;
    }

    // Getters and Setters
    public String getQuestionID() { return questionID; }
    public void setQuestionID(String questionID) { this.questionID = questionID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}