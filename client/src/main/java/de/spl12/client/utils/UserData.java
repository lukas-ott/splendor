package de.spl12.client.utils;

public class UserData {
    private int id;
    private String username;
    private String password;
    private int age;


    public UserData() {

    }

    /**
     * Constructor for UserData class.
     * @param id
     * @param username
     * @param password
     * @param age
     */
    public UserData(int id, String username, String password, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {return age; }
}