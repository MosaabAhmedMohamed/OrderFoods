package com.example.mosaab.orderfoods.Model;

public class Order {

    private String User_Phone;
    private String ProudctId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;


    public Order()
    {

    }

    public Order(String user_Phone, String proudctId, String productName, String quantity, String price, String discount, String image) {
        User_Phone = user_Phone;
        ProudctId = proudctId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }

    public String getUser_Phone() {
        return User_Phone;
    }

    public void setUser_Phone(String user_Phone) {
        User_Phone = user_Phone;
    }

    public String getProudctId() {
        return ProudctId;
    }

    public void setProudctId(String proudctId) {
        ProudctId = proudctId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

}
