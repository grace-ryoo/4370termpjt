package termProject.models;

public class GroceryItem {

    private final int itemId;

    private final String name;

    private final int quantity;

    private final User user;

    private boolean isBought;

    /**
     * Constructs a new GroceryItem with the specified details.
     *
     * @param itemId the unique identifier for the grocery item
     * @param name the name of the grocery item
     * @param quantity the quantity of the grocery item
     * @param user the user who owns this grocery item
     */
    public GroceryItem(int itemId, String name, int quantity, User user) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.user = user;
        this.isBought = false;
    }

    /**
     * Returns the item ID.
     *
     * @return the item ID
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Returns the name of the grocery item.
     *
     * @return the name of the grocery item
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the quantity of the grocery item.
     *
     * @return the quantity of the grocery item
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the user who owns this grocery item.
     *
     * @return the user who owns this grocery item
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the isBought status of the grocery item.
     *
     * @return the isBought status of the grocery item
     */
    public boolean getIsBought() {
        return isBought;
    }

    /*
    * Sets isBought status of the grocery item.
     */
    public void setIsBought(boolean isBought) {
        this.isBought = isBought;
    }

}
