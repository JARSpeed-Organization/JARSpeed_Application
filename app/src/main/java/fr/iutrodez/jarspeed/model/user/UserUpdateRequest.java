package fr.iutrodez.jarspeed.model.user;

import java.io.Serializable;
import java.util.Date;

import fr.iutrodez.jarspeed.model.gender.Gender;

public class UserUpdateRequest implements Serializable {
    private String email;
    private String lastname;
    private String firstname;
    private String password;
    private Double weight;
    private String birthdate;
    private Gender gender; // Ajoute ce champ

    public UserUpdateRequest(){}

    public UserUpdateRequest(String email, String lastname, String firstname, String password, Double weight, String birthdate, Gender gender) {
        this.email = email;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.weight = weight;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    // Ajouter getters et setters pour Gender
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
