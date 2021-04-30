package com.example.smartspace;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String Email;
    private String password;
    private String firstName;
    private String secondName;
   // private List<Booking> bookings;

    public User(){

    }

    public User(String email, String password, String fname, String sname /*List<Booking> b*/) {
        this.Email = email;
        this.password = password;
        this.firstName=fname;
        this.secondName=sname;
     //   this.bookings=b;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   // public List<Booking> getBookings() {
    //    return bookings;
   // }

   // public void setBookings(List<Booking> bookings) {
    //    this.bookings = bookings;
    //}
}
