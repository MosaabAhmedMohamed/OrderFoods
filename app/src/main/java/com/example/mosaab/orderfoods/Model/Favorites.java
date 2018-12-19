package com.example.mosaab.orderfoods.Model;

public class Favorites {

   private String FoodId,FoodName,FoodPrice,FoodMenuId,FoodIamge,FoodDiscount,FoodDescription,UserPhone;

    public Favorites() {
    }

    public Favorites(String foodId, String foodName, String foodPrice, String foodMenuId, String foodIamge, String foodDiscount, String foodDescription, String userPhone) {
        FoodId = foodId;
        FoodName = foodName;
        FoodPrice = foodPrice;
        FoodMenuId = foodMenuId;
        FoodIamge = foodIamge;
        FoodDiscount = foodDiscount;
        FoodDescription = foodDescription;
        UserPhone = userPhone;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodMenuId() {
        return FoodMenuId;
    }

    public void setFoodMenuId(String foodMenuId) {
        FoodMenuId = foodMenuId;
    }

    public String getFoodIamge() {
        return FoodIamge;
    }

    public void setFoodIamge(String foodIamge) {
        FoodIamge = foodIamge;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
