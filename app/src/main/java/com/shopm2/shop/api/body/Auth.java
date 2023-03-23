package com.shopm2.shop.api.body;

public class Auth {
    final String username;
    final String password;
    public Auth(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
