package fr.iutrodez.jarspeed.model;

public class User {
    private Integer id;
    private String email;
    private String lastname;
    private String firstname;
    private Integer age;
    //private Gender gender;  // Vous pouvez définir une classe Gender séparée si nécessaire
    private Double weight;
    private String password; // Notez que stocker le mot de passe en clair n'est pas recommandé

    // Constructeur vide pour initialiser un objet User sans paramètres
    public User() {
    }

    // Constructeur avec tous les paramètres
    public User(String lastname, String firstname, String email, Integer age/* , Gender gender*/, Double weight, String password) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.age = age;
        //this.gender = gender;
        this.weight = weight;
        this.password = password;
    }

    // Getters et setters pour chaque attribut
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

//    public Gender getGender() {
//        return gender;
//    }
//
//    public void setGender(Gender gender) {
//        this.gender = gender;
//    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
