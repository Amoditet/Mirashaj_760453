/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

public class Restaurant {
    private int id;
    private String name;
    private String address;
    private String location;
    private String cuisine;
    private double rating;
    private int reviews;
    private String price;
    private String imageUrl;

    private String phoneNumber;
    private String award;
    private String greenStar;
    private String facilities;
    private String description;
    private String owner;

    public Restaurant(int id, String name, String address, double rating, int reviews, String location, String cuisine,
            String price,
            String imageUrl, String phoneNumber, String award, String greenStar, String facilities,
            String description, String owner) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
        this.price = price;
        this.cuisine = cuisine;
        this.rating = rating;
        this.reviews = reviews;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.award = award;
        this.greenStar = greenStar;
        this.facilities = facilities;
        this.description = description;
        this.owner = owner;
    }

    public String getDistance() {
        return location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getCuisine() {
        return cuisine;
    }

    public double getRating() {
        return rating;
    }

    public int getReviews() {
        return reviews;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAward() {
        return award;
    }

    public String getGreenStar() {
        return greenStar;
    }

    public String getFacilities() {
        return facilities;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Restaurant))
            return false;
        Restaurant r = (Restaurant) o;
        if (name == null && r.name == null)
            return true;
        if (name == null || r.name == null)
            return false;
        return name.equalsIgnoreCase(r.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.toLowerCase().hashCode();
    }

}