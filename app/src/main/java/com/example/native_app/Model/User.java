package com.example.native_app.Model;

public class User {
    private String name;
    private int age;
    private boolean premium;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, int age, boolean premium) {
        this.name = name;
        this.age = age;
        this.premium = premium;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isPremium() {
        return premium;
    }
}
