package com.example.mosaab.orderfoods.Model;

import java.util.List;

public class Request {
    private String phone;
    private String adress;
    private String toatl;
    private String name;
    private String status;
    private String comment;
    private String Payment_Method;
    private String Payment_State;
    private String LatLng = "";
    private List<Order> foods;

    public Request()
    {

    }

    public Request(String phone, String adress, String toatl, String name, String status, String comment, String payment_Method, String payment_State, String latLng, List<Order> foods) {
        this.phone = phone;
        this.adress = adress;
        this.toatl = toatl;
        this.name = name;
        this.status = status;
        this.comment = comment;
        Payment_Method = payment_Method;
        Payment_State = payment_State;
        LatLng = latLng;
        this.foods = foods;
    }


    public String getPayment_State() {
        return Payment_State;
    }

    public void setPayment_State(String payment_State) {
        Payment_State = payment_State;
    }

    public String getPayment_Method() {
        return Payment_Method;
    }

    public void setPayment_Method(String payment_Method) {
        Payment_Method = payment_Method;
    }

    public String getLatLng() {
        return LatLng;
    }

    public void setLatLng(String latLng) {
        LatLng = latLng;
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
