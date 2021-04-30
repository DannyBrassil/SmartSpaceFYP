package com.example.smartspace;

import java.util.ArrayList;

public class Carpark {
    private String id;
    private String email;
    private String password;
    private String name;
    private double longitude;
    private double latitude;
    private int spaces;
    private int spaceAvailable;
    private int floors;
    private boolean smartspace;
    private boolean smartprice;
   private ArrayList<CarParkPrice> prices;
    private ArrayList<CarParkPrice> smartPrices;
    private ArrayList<CarParkTime> times;
    private String d;
    private ArrayList<String> ammenity;
    private ArrayList<Layout> layout;
    private ArrayList<Review> reviews;

    public Carpark(){

    }
    public Carpark(String id, String email, String password, String name, double longitude, double latitude, int spaces, int spaceAvailable, int floors, boolean smartspace, boolean smartprice, ArrayList<CarParkPrice> p, ArrayList<CarParkPrice> smartPrices, ArrayList<CarParkTime> t, String description,  ArrayList<String> a, ArrayList<Layout> l) {
        this.id=id;
        this.email = email;
        this.password= password;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.spaces = spaces;
        this.spaceAvailable = spaceAvailable;
        this.floors=floors;
        this.smartspace=smartspace;
        this.smartprice=smartprice;
        this.prices=p;
        this.smartPrices = smartPrices;
        this.times=t;
        this.d=description;//car park ammenities and description
        this.ammenity=a;
        this.layout = l;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }

    public int getSpaceAvailable() {
        return spaceAvailable;
    }

    public void setSpaceAvailable(int spaceAvailable) {
        this.spaceAvailable = spaceAvailable;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public boolean isSmartspace() {
        return smartspace;
    }

    public void setSmartspace(boolean smartspace) {
        this.smartspace = smartspace;
    }

    public ArrayList<CarParkPrice> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<CarParkPrice> prices) {
        this.prices = prices;
    }

    public ArrayList<CarParkPrice> getSmartPrices() {
        return smartPrices;
    }

    public void setSmartPrices(ArrayList<CarParkPrice> smartPrices) {
        this.smartPrices = smartPrices;
    }

    public ArrayList<CarParkTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<CarParkTime> times) {
        this.times = times;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public ArrayList<String> getAmmenity() {
        return ammenity;
    }

    public void setAmmenity(ArrayList<String> ammenity) {
        this.ammenity = ammenity;
    }


    public boolean isSmartprice() {
        return smartprice;
    }

    public void setSmartprice(boolean smartprice) {
        this.smartprice = smartprice;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Layout> getLayout() {
                    return layout;
                }

                public void setLayout(ArrayList<Layout> l) {
                    this.layout = l;
                }

    //distance between two Coordinates
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}


