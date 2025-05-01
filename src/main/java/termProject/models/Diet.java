package termProject.models;
public class Diet {
    private int dietId;
    private String dietName;
    private String dietDescription;

    public Diet(int dietId, String dietName, String dietDescription) {
        this.dietId = dietId;
        this.dietName = dietName;
        this.dietDescription = dietDescription;
    }

    // Getters
    public int getDietId() {
        return dietId;
    }

    public String getDietName() {
        return dietName;
    }

    public String getDietDescription() {
        return dietDescription;
    }
}