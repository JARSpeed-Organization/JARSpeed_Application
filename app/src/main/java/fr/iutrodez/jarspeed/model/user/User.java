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
     * The Birthday.
     */
    private Date birthday;
    /**
     * The Gender.
     */
    private Gender gender;
    /**
     * The Weight.
     */
//private Gender gender;  // Vous pouvez définir une classe Gender séparée si nécessaire
    private Double weight;
    /**
     * The Password.
     */
    private String password; // Notez que stocker le mot de passe en clair n'est pas recommandé

    /**
     * Instantiates a new User.
     */
// Constructeur vide pour initialiser un objet User sans paramètres
    public User() {
    }

    /**
     * Instantiates a new User.
     *
     * @param lastname  the lastname
     * @param firstname the firstname
     * @param email     the email
     * @param birthday  the birthday
     * @param weight    the weight
     * @param password  the password
     */
// Constructeur avec tous les paramètres
    public User(String lastname, String firstname, String email, Date birthday/* , Gender gender*/, Double weight, String password) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.birthday = birthday;
        //this.gender = gender;
        this.weight = weight;
        this.password = password;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
// Getters et setters pour chaque attribut
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
     * Gets birthday.
     *
     * @return the birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Sets birthday.
     *
     * @param pBirthday the p birthday
     */
    public void setBirthday(Date pBirthday) {
        birthday = pBirthday;
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

}
