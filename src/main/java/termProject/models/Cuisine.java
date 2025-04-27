package termProject.models;

public class Cuisine {
    /**
     * Unique identifier for the cuisine.
     */
    private final String cuisineId;

    /**
     * Name of the cuisine.
     */
    private final String cuisineName;

    /**
     * Constructs a new Cuisine with the specified ID and name.
     *
     * @param cuisineId   the unique identifier for the cuisine
     * @param cuisineName the name of the cuisine
     */
    public Cuisine(String cuisineId, String cuisineName) {
        this.cuisineId = cuisineId;
        this.cuisineName = cuisineName;
    }

    /**
     * Returns the cuisine ID.
     *
     * @return the cuisine ID
     */
    public String getCuisineId() {
        return cuisineId;
    }

    /**
     * Returns the cuisine name.
     *
     * @return the cuisine name
     */
    public String getCuisineName() {
        return cuisineName;
    }
}
