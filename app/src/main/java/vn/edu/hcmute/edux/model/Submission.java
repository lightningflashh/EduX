package vn.edu.hcmute.edux.model;

import java.util.List;

public class Submission {
    private String userId;
    private String quizId;
    private List<String> answers;
    private int score;
    private long timeTaken; // Thời gian làm bài (milliseconds)

    public Submission() {
    }

    public Submission(String userId, String quizId, List<String> answers, int score, long timeTaken) {
        this.userId = userId;
        this.quizId = quizId;
        this.answers = answers;
        this.score = score;
        this.timeTaken = timeTaken;
    }

    // Getters và Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
}