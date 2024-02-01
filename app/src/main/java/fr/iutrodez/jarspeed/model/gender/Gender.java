package fr.iutrodez.jarspeed.model.gender;

public class Gender {
    private Integer id;
    private String label;

    // Constructeur par défaut nécessaire pour la sérialisation/désérialisation
    public Gender() {}

    public Gender(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    // Getters et setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
