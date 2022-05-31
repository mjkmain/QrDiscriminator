package com.example.myapplication;

public class UserToken {
    private int tokenNumber;
    private int menuPrice;
//    private int menuTotalPrice;
    private String payMethod;
    private String menuName;


    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int menuNumber) {
        this.tokenNumber = menuNumber;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }
}
