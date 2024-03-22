package fr.iutrodez.jarspeed.model.user;

import java.util.Date;

import fr.iutrodez.jarspeed.model.gender.Gender;

/**
 * The type User.
 */
public class User {
    /**
     * The Id.
     */
    private Integer id;
    /**
     * The Email.
     */
    private String email;
    /**
     * The Lastname.
     */
    private String lastname;
    /**
     * The Firstname.
     */
    private String firstname;
    /**
     * The Birthdate.
     */
    private Date birthdate;
    /**
     * The Gender.
     */
    private Gender gender;
    /**
     * The Weight.
     */
    private Double weight;
    /**
     * The Password.
     */
    private String password; // Notez que stocker le mot de passe en clair n'est pas recommandé
    /**
     * The Token.
     */
    private String token;

    /**
     * Instantiates a new User.
     */
    public User() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets lastname.
     *
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets lastname.
     *
     * @param lastname the lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Gets firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets firstname.
     *
     * @param firstname the firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets birthdate.
     *
     * @return the birthdate
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * Sets birthdate.
     *
     * @param pBirthdate the p birthdate
     */
    public void setBirthdate(Date pBirthdate) {
        birthdate = pBirthdate;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param pGender the p gender
     */
    public void setGender(Gender pGender) {
        gender = pGender;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets weight.
     *
     * @param weight the weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param pToken the p token
     */
    public void setToken(String pToken) {
        token = pToken;
    }
}
