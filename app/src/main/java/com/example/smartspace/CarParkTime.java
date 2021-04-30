package com.example.smartspace;

public class CarParkTime {
    String start;
    String end;
    boolean hours24;
    boolean closed;

    public CarParkTime() {
    }


    public CarParkTime(String start, String end, boolean hours24, boolean closed) {
        this.start = start;
        this.end = end;
        this.hours24 = hours24;
        this.closed = closed;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isHours24() {
        return hours24;
    }

    public void setHours24(boolean hours24) {
        this.hours24 = hours24;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
