package com.example.omrproject.Model;

import java.util.List;
import java.util.Map;

public class Table {
    private String location, numberOfSeat, occupied;
    private List<Order> foods;

    public Table() {
    }

    public Table(String location, String numberOfSeat, String occupied, List<Order> foods) {
        this.location = location;
        this.numberOfSeat = numberOfSeat;
        this.occupied = occupied;
        this.foods = foods;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumberOfSeat() {
        return numberOfSeat;
    }

    public void setNumberOfSeat(String numberOfSeat) {
        this.numberOfSeat = numberOfSeat;
    }

    public String getOccupied() {
        return occupied;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
