package vn.edu.hcmute.edux.model;

public class Answer {
    private String answerID;
    private String content;
    private Boolean correctAnswer;

    // Constructor
    public Answer(String answerID, String content, Boolean correctAnswer) {
        this.answerID = answerID;
        this.content = content;
        this.correctAnswer = correctAnswer;
    }

    // Getters and Setters
    public String getAnswerID() { return answerID; }
    public void setAnswerID(String answerID) { this.answerID = answerID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(Boolean correctAnswer) { this.correctAnswer = correctAnswer; }
}