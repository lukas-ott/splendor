package de.spl12.server.application;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a user and saves connected attributes
 *
 * @author luott
 */
public class User {

  @JsonProperty("id")
  private int id;

  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  private String password;

  @JsonProperty("age")
  private int age;

  public User() {
  }

  /**
   * Constructs a new User object with the specified parameters.
   *
   * @param id       The unique identifier for the user.
   * @param username The username of the user.
   * @param password The password of the user (hashed or plaintext depending on the
   *                 implementation).
   * @param age      The age of the user.
   */
  public User(int id, String username, String password, int age) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.age = age;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }
}