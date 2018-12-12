package com.example.mosaab.orderfoods.Model;

public class Rating {

    private String User_Phone;
    private String food_ID;
    private String rate_Value;
    private String comment;

    public Rating() {
    }

    public Rating(String user_Phone, String food_ID, String rate_Value, String comment) {
        User_Phone = user_Phone;
        this.food_ID = food_ID;
        this.rate_Value = rate_Value;
        this.comment = comment;
    }

    public String getUser_Phone() {
        return User_Phone;
    }

    public void setUser_Phone(String user_Phone) {
        User_Phone = user_Phone;
    }

    public String getFood_ID() {
        return food_ID;
    }

    public void setFood_ID(String food_ID) {
        this.food_ID = food_ID;
    }

    public String getRate_Value() {
        return rate_Value;
    }

    public void setRate_Value(String rate_Value) {
        this.rate_Value = rate_Value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
