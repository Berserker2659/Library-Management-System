package library;

import java.util.Date;

public class Member {
    private int id;
    private String name;
    private String username; // Added
    private String password; // Added
    private double owes_money;

    public Member(int id, String name, double owes_money, String username, String password) {
        this.id = id;
        this.name = name;
        this.owes_money = owes_money;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOwes_money() {
        return owes_money;
    }

    public void setOwes_money(double owes_money) {
        this.owes_money = owes_money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
