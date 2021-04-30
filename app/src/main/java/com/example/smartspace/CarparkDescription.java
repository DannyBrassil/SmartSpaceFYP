package com.example.smartspace;

import java.util.ArrayList;

public class CarparkDescription {
    String description;
   private ArrayList<String> ammenities;

    public CarparkDescription() {

    }
    public CarparkDescription(String info, ArrayList<String> names) {
        this.description=info;
        this.ammenities = names;
    }

    public ArrayList<String> getAmmenities() {
        return ammenities;
    }

    public void setAmmenities(ArrayList<String> names) {
        this.ammenities = names;
    }
}
