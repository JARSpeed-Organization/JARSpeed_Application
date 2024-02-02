package fr.iutrodez.jarspeed.model.gender;

/**
 * The type Gender.
 */
public class Gender {
    /**
     * The Id.
     */
    private Integer id;
    /**
     * The Label.
     */
    private String label;

    /**
     * Instantiates a new Gender.
     */
// Constructeur par défaut nécessaire pour la sérialisation/désérialisation
    public Gender() {}

    /**
     * Instantiates a new Gender.
     *
     * @param id    the id
     * @param label the label
     */
    public Gender(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
// Getters et setters
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
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return label;
    }


}
