package com.example.omrproject.Model;

public class Food {
    private String name, description, discount, img, menuId, price;

    public Food() {
    }

    public Food(String name, String description, String discount, String img, String menuId, String price) {
        this.name = name;
        this.description = description;
        this.discount = discount;
        this.img = img;
        this.menuId = menuId;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
