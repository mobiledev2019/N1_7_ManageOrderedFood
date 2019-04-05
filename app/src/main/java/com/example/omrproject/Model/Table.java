package com.example.omrproject.Model;

import java.util.List;
import java.util.Map;

public class Table {
    private String location, numberOfSeat, occupied, type, typeCapSt, capSt;
    private List<Order> foods;

    public Table() {
    }

    public Table(String location, String numberOfSeat, String occupied, String type, String typeCapSt, String capSt, List<Order> foods) {
        this.location = location;
        this.numberOfSeat = numberOfSeat;
        this.occupied = occupied;
        this.type = type;
        this.typeCapSt = typeCapSt;
        this.capSt = capSt;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCapSt() {
        return typeCapSt;
    }

    public void setTypeCapSt(String typeCapSt) {
        this.typeCapSt = typeCapSt;
    }

    public String getCapSt() {
        return capSt;
    }

    public void setCapSt(String capSt) {
        this.capSt = capSt;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
