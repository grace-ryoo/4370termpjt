package termProject.models;

public class Cuisine {
    private int cuisineId;
    private String cuisineName;

    public Cuisine(int cuisineId, String cuisineName) {
        this.cuisineId = cuisineId;
        this.cuisineName = cuisineName;
    }

    public int getCuisineId() {
        return cuisineId;
    }

    public String getCuisineName() {
        return cuisineName;
    }
}
