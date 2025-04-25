package termProject.models;

public class Diet {
    /**
     * Unique identifier for the diet.
     */
    private final String dietId;

    /**
     * Name of the diet.
     */
    private final String dietName;

    /**
     * Constructs a new Diet with the specified ID and name.
     *
     * @param dietId   the unique identifier for the diet
     * @param dietName the name of the diet
     */
    public Diet(String dietId, String dietName) {
        this.dietId = dietId;
        this.dietName = dietName;
    }

    /**
     * Returns the diet ID.
     *
     * @return the diet ID
     */
    public String getDietId() {
        return dietId;
    }

    /**
     * Returns the diet name.
     *
     * @return the diet name
     */
    public String getDietName() {
        return dietName;
    }
}
