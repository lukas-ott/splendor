package de.spl12.domain;

import java.io.Serializable;

/**
 * Represents a user account.
 *
 * @author leon.kuersch
 */
public class User implements Serializable {
  private static final long serialVersionUID = 8977055827986458981L;
  private final int id;
  private final String username;
  private final String password;
  private final int age;

  /**
   * Constructs a User object with the specified id, username, password, and age.
   *
   * @param id the unique identifier for the user.
   * @param username the username of the user.
   * @param password the password of the user.
   * @param age the age of the user.
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

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public int getAge() {
    return age;
  }
}
