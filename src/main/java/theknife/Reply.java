/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

public class Reply {
    private String ownerUsername;
    private String reviewUsername;
    private int restaurantId;
    private String text;

    public Reply(String ownerUsername, String reviewUsername, int restaurantId, String text) {
        this.ownerUsername = ownerUsername;
        this.reviewUsername = reviewUsername;
        this.restaurantId = restaurantId;
        this.text = text;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getReviewUsername() {
        return reviewUsername;
    }

    public void setReviewUsername(String reviewUsername) {
        this.reviewUsername = reviewUsername;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
