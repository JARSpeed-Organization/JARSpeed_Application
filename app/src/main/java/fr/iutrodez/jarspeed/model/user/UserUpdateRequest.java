package fr.iutrodez.jarspeed.model.user;

import java.io.Serializable;
import java.util.Date;

import fr.iutrodez.jarspeed.model.gender.Gender;

/**
 * The type User update request.
 */
public class UserUpdateRequest implements Serializable {
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
     * The Password.
     */
    private String password;
    /**
     * The Weight.
     */
    private Double weight;
    /**
     * The Birthdate.
     */
    private String birthdate;
    /**
     * The Gender.
     */
    private Gender gender; // Ajoute ce champ

    /**
     * Instantiates a new User update request.
     */
    public UserUpdateRequest(){}

    /**
     * Instantiates a new User update request.
     *
     * @param email     the email
     * @param lastname  the lastname
     * @param firstname the firstname
     * @param password  the password
     * @param weight    the weight
     * @param birthdate the birthdate
     * @param gender    the gender
     */
    public UserUpdateRequest(String email, String lastname, String firstname, String password, Double weight, String birthdate, Gender gender) {
        this.email = email;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.weight = weight;
        this.birthdate = birthdate;
        this.gender = gender;
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
     * Gets birthdate.
     *
     * @return the birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Sets birthdate.
     *
     * @param birthdate the birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Gets gender.
     *
     * @return the gender
     */
// Ajouter getters et setters pour Gender
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
