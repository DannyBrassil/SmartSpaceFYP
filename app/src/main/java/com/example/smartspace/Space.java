package com.example.smartspace;

public class Space {
    int layoutposition;
    boolean active;

    public Space(){

    }

    public Space(int layoutposition, boolean active) {
        this.layoutposition = layoutposition;
        this.active = active;
    }

    public int getLayoutposition() {
        return layoutposition;
    }

    public void setLayoutposition(int layoutposition) {
        this.layoutposition = layoutposition;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
