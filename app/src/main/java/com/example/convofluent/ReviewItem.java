package com.example.convofluent;

public class ReviewItem {
    private String category;
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private String explanation;

    public ReviewItem(String category, String question, String userAnswer, String correctAnswer, String explanation) {
        this.category = category;
        this.question = question;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public String getCategory() { return category; }
    public String getQuestion() { return question; }
    public String getUserAnswer() { return userAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
}
