package com.example.smartspace;

public class Review {

    int stars;
    String feedback;

    public Review(){

    }
    public Review(int stars, String feedback){
        this.feedback=feedback;
        this.stars=stars;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
