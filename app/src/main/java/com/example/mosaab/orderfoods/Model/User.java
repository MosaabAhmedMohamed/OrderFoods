package com.example.mosaab.orderfoods.Model;

public class User {

    private String Name;
    private String Password;
    private  String Phone;
    private String IsStaff;
    private String secureCode;



    public User()
    {

    }

    public User(String name, String password,String secure_code) {
        Name = name;
        Password = password;
        IsStaff="false";
        secureCode = secure_code;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

}
