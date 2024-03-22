package fr.iutrodez.jarspeed.model.user;

/**
 * The object return on connection.
 */
public class Login {
    /**
     * The Refresh token.
     */
    private String refreshToken;
    /**
     * The User.
     */
    private User user;

    /**
     * Instantiates a new Login.
     */
    public Login() {
    }

    /**
     * Gets refresh token.
     *
     * @return the refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets refresh token.
     *
     * @param pRefreshToken the p refresh token
     */
    public void setRefreshToken(String pRefreshToken) {
        refreshToken = pRefreshToken;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param pUser the p user
     */
    public void setUser(User pUser) {
        user = pUser;
    }
}
