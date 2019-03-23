package com.example.omrproject.Model;

import java.util.List;

public class Bill {
    private String staffId, staffName, total, date, tableId;
    private List<Order> foods;

    public Bill() {
    }

    public Bill(String staffId, String staffName, String total, String date, String tableId, List<Order> foods) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.total = total;
        this.date = date;
        this.tableId = tableId;
        this.foods = foods;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
