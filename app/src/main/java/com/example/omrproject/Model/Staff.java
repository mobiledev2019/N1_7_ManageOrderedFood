package com.example.omrproject.Model;

public class Staff {
    private String fullName, address, password, gender, ship, location;

    public Staff() {
    }

    public Staff(String fullName, String address, String password, String gender, String ship, String location) {
        this.fullName = fullName;
        this.address = address;
        this.password = password;
        this.gender = gender;
        this.ship = ship;
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShip() {
        return ship;
    }

    public void setShip(String ship) {
        this.ship = ship;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
