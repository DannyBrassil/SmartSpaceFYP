package com.example.smartspace;

import java.util.Date;

public class Booking {

    private String carparkid;
    private String location;
    private Date date;
    private int time;
    private Boolean active;
    private Boolean started;
    private Boolean review;


    public Booking(){

    }

    public Booking(String carparkid,String location, Date date, int time, Boolean active, Boolean started, Boolean review) {
       this.carparkid=carparkid;
        this.location = location;
        this.date = date;
        this.time = time;
        this.active = active;
        this.started=started;
        this.review=review;
    }

    public String getCarparkid() {
        return carparkid;
    }

    public void setCarparkid(String carparkid) {
        this.carparkid = carparkid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }
}

