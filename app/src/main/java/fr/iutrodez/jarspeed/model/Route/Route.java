package fr.iutrodez.jarspeed.model.Route;

public class Route {
    private String title;
    private String date;
    private String time;

    // Constructeur
    public Route(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    // Getters et Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Vous pouvez ajouter d'autres propriétés et méthodes selon vos besoins
}
