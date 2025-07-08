/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

public class Review {
    private String username;
    private String restaurantName;
    private int restaurantId;
    private int rating;
    private String text;

    public Review(String username, String restaurantName, int restaurantId, int rating, String text) {
        this.username = username;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.text = text;
    }

    public Review(String username, int restaurantId, int rating, String text) {
        this(username, null, restaurantId, rating, text);
    }

    public String getUsername() {
        return username;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int r) {
        this.rating = r;
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        this.text = t;
    }
}
