/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

public class Bookings {
    private String restaurantName;
    private String date;
    private String time;

    public Bookings(String restaurantName, String date, String time) {
        this.restaurantName = restaurantName;
        this.date = date;
        this.time = time;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Bookings))
            return false;
        Bookings booking = (Bookings) o;
        return restaurantName.equals(booking.restaurantName) &&
                date.equals(booking.date) &&
                time.equals(booking.time);
    }

    @Override
    public int hashCode() {
        int result = restaurantName.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + time.hashCode();
        return result;
    }
}
