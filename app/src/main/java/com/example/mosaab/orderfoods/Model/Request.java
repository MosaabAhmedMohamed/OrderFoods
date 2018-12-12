package com.example.mosaab.orderfoods.Model;

import java.util.List;

public class Request {
    private String phone;
    private String adress;
    private String toatl;
    private String name;
    private String status;
    private String comment;
    private List<Order> foods;

    public Request()
    {

    }

    public Request(String phone, String adress, String toatl, String name, String status, String comment, List<Order> foods) {
        this.phone = phone;
        this.adress = adress;
        this.toatl = toatl;
        this.name = name;
        this.status = status;
        this.comment = comment;
        this.foods = foods;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getToatl() {
        return toatl;
    }

    public void setToatl(String toatl) {
        this.toatl = toatl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
