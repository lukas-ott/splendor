package de.spl12.client.utils;

public class User {

    private UserData userData;

    private User() {}

    /**
     * SingletonHelper is a private static nested class that assists in the Singleton pattern
     * implementation for the {@code User} class. It is responsible for holding the singleton
     * instance of {@code User} and ensuring thread-safe lazy initialization without the need for
     * synchronized blocks or explicit locking mechanisms.
     *
     * The singleton instance of {@code User} is created when the {@code SingletonHelper} class
     * is first accessed. This leverages the class loading mechanism to ensure that the instance
     * is only created once and only when needed.
     *
     * This approach is commonly known as the "Initialization-on-demand holder idiom" and is a
     * preferred method for implementing the Singleton pattern in Java.
     */
    private static class SingletonHelper {
        private static final User INSTANCE = new User();
    }

    public static User getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
