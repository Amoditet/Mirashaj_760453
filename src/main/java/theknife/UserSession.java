/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserSession {
    private static UserSession instance;

    private String username;
    private String location;
    private String role;
    private List<Restaurant> favorites = new ArrayList<>();
    private List<Bookings> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private List<Restaurant> ownedRestaurants = new ArrayList<>();
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private List<Reply> replies = new ArrayList<>();

    private static final String BOOKING_CSV_PATH = "data/bookings_list.csv";
    private static final String FAVORITES_CSV_PATH = "data/favorites_list.csv";
    private static final String REVIEWS_CSV_PATH = "data/reviews_list.csv";
    private static final String REPLIES_CSV_PATH = "data/replies_list.csv";

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    public void addReview(Review review) {
        if (review != null) {
            reviews.add(review);
            saveReviewsToCSV();
        }
    }

    public void addOrUpdateReview(int restaurantId, Review review) {
        if (review != null) {
            reviews.removeIf(r -> r.getRestaurantId() == restaurantId && r.getUsername().equals(username));
            reviews.add(review);
            saveReviewsToCSV();
        }
    }

    public void deleteReview(String restaurantName) {
        reviews.removeIf(r -> r.getRestaurantName().equals(restaurantName) && r.getUsername().equals(username));
        saveReviewsToCSV();
    }

    public void setUser(String username, String location, String role) {
        this.username = username;
        this.location = location;
        this.role = role;
        this.loadAllRestaurants();
        this.loadOwnedRestaurantsFromCSV();
        this.loadBookingsFromCSV();
        this.loadFavoritesFromCSV();
        this.loadReviewsFromCSV();
        this.loadRepliesFromCSV();
    }

    private void loadBookingsFromCSV() {
        bookings.clear();

        InputStream inputStream = null;
        try {
            File externalFile = new File(BOOKING_CSV_PATH);
            if (externalFile.exists()) {
                inputStream = new FileInputStream(externalFile);
            } else {
                inputStream = getResourceStream(BOOKING_CSV_PATH);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String user = parts[0].trim();
                        String restaurantName = parts[1].trim();
                        String date = parts[2].trim();
                        String time = parts[3].trim();
                        if (user.equals(username)) {
                            bookings.add(new Bookings(restaurantName, date, time));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cleanExpiredBookings();
    }

    private void cleanExpiredBookings() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        boolean hasExpiredBookings = false;

        List<Bookings> validBookings = new ArrayList<>();
        for (Bookings booking : bookings) {
            try {
                LocalDate bookingDate = LocalDate.parse(booking.getDate(), dateFormatter);
                LocalTime bookingTime = LocalTime.parse(booking.getTime(), timeFormatter);
                LocalDateTime bookingDateTime = LocalDateTime.of(bookingDate, bookingTime);

                // Mantieni la prenotazione se data+ora è futura
                if (!bookingDateTime.isBefore(currentDateTime)) {
                    validBookings.add(booking);
                } else {
                    hasExpiredBookings = true;
                }
            } catch (Exception e) {
                // Se c'è un errore nel parsing, mantieni la prenotazione
                validBookings.add(booking);
            }
        }

        if (hasExpiredBookings) {
            bookings.clear();
            bookings.addAll(validBookings);
            cleanExpiredBookingsFromCSV();
        }
    }

    private void cleanExpiredBookingsFromCSV() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        File file = new File(BOOKING_CSV_PATH);
        List<String> lines = new ArrayList<>();
        lines.add("username,restaurantName,date,time");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String date = parts[2].trim();
                        String time = parts[3].trim();
                        try {
                            LocalDate bookingDate = LocalDate.parse(date, dateFormatter);
                            LocalTime bookingTime = LocalTime.parse(time, timeFormatter);
                            LocalDateTime bookingDateTime = LocalDateTime.of(bookingDate, bookingTime);

                            // Mantieni solo le prenotazioni future (data+ora)
                            if (!bookingDateTime.isBefore(currentDateTime)) {
                                lines.add(line);
                            }
                        } catch (Exception e) {
                            // Se c'è un errore nel parsing, mantieni la riga
                            lines.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadReviewsFromCSV() {
        reviews.clear();

        InputStream inputStream = null;
        try {
            File externalFile = new File(REVIEWS_CSV_PATH);
            if (externalFile.exists()) {
                inputStream = new FileInputStream(externalFile);
            } else {
                inputStream = getResourceStream(REVIEWS_CSV_PATH);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (parts.length < 4) {
                        continue;
                    }
                    String user = parts[0].trim();
                    String restaurantIdStr = parts[1].trim();
                    int restaurantId;
                    try {
                        restaurantId = Integer.parseInt(restaurantIdStr);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    int rating;
                    String reviewText;
                    try {
                        rating = Integer.parseInt(parts[2].trim());
                        reviewText = parts[3].replaceAll("^\"|\"$", "").replace("\"\"", "\"");
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    String restaurantName = null;
                    for (Restaurant r : allRestaurants) {
                        if (r.getId() == restaurantId) {
                            restaurantName = r.getName();
                            break;
                        }
                    }
                    if (restaurantName == null) {
                        for (Restaurant r : ownedRestaurants) {
                            if (r.getId() == restaurantId) {
                                restaurantName = r.getName();
                                break;
                            }
                        }
                    }
                    if (restaurantName == null) {
                        restaurantName = "Unknown Restaurant";
                    }

                    Review review = new Review(user, restaurantName, restaurantId, rating, reviewText);
                    reviews.add(review);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReviewsToCSV() {
        File file = new File(REVIEWS_CSV_PATH);
        List<String> lines = new ArrayList<>();
        lines.add("username,restaurantId,rating,reviewText");
        for (Review review : reviews) {
            int restaurantId = getRestaurantIdByName(review.getRestaurantName());
            String line = String.format("%s,%d,%d,\"%s\"",
                    review.getUsername(),
                    restaurantId,
                    review.getRating(),
                    review.getText().replace("\"", "\"\""));
            lines.add(line);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearUser() {
        this.username = null;
        this.location = null;
        this.role = null;
        bookings.clear();
        favorites.clear();
    }

    public boolean isLoggedIn() {
        return username != null;
    }

    public boolean isNotLoggedIn() {
        return username == null;
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public String getRole() {
        return role;
    }

    public void updateUserLocation(String newLocation) {
        if (!isLoggedIn() || newLocation == null) {
            return;
        }

        this.location = newLocation;

        saveUserLocationToCSV(newLocation);
    }

    private void saveUserLocationToCSV(String newLocation) {
        java.nio.file.Path csvPath = java.nio.file.Paths.get("data", "utenti.csv");

        try {
            List<String> lines = new ArrayList<>();

            // Prima prova a leggere dal file esterno
            java.io.File externalFile = new java.io.File("data/utenti.csv");
            if (externalFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(externalFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                }
            } else {
                // Se il file esterno non esiste, prova dalle risorse
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(getResourceStream("data/utenti.csv")))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (IOException e) {
                    // Se anche le risorse non esistono, crea un file con header base
                    lines.add("Name,Surname,Username,Password,Location,Role");
                }
            }

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (i == 0)
                    continue;

                String[] fields = line.split(",", -1);
                if (fields.length >= 6 && fields[2].equals(username)) {
                    fields[4] = newLocation;
                    lines.set(i, String.join(",", fields));
                    break;
                }
            }

            java.nio.file.Files.write(csvPath, lines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFavorite(Restaurant restaurant) {
        if (restaurant == null)
            return;
        boolean alreadyPresent = favorites.stream()
                .anyMatch(r -> r.getName() != null && r.getName().trim().replaceAll("^\"|\"$", "")
                        .equalsIgnoreCase(restaurant.getName().trim().replaceAll("^\"|\"$", "")));
        if (!alreadyPresent) {
            favorites.add(restaurant);
            appendFavoriteToCSV(restaurant);
        }
    }

    private void appendFavoriteToCSV(Restaurant restaurant) {
        File file = new File(FAVORITES_CSV_PATH);
        try {
            List<String> existing = new ArrayList<>();
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean first = true;
                    while ((line = br.readLine()) != null) {
                        if (first) {
                            first = false;
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            existing.add(parts[0].trim() + "," + parts[1].replaceAll("\"", "").trim());
                        }
                    }
                }
            }

            String newLine = username + "," + restaurant.getName();
            boolean exists = existing.stream()
                    .anyMatch(l -> l.equalsIgnoreCase(newLine));
            if (!exists) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                    if (file.length() == 0) {
                        bw.write("username,restaurantName");
                        bw.newLine();
                    }
                    bw.write(newLine);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFavorite(Restaurant restaurant) {
        if (restaurant == null)
            return;
        favorites.removeIf(r -> r.getName() != null && r.getName().trim().replaceAll("^\"|\"$", "")
                .equalsIgnoreCase(restaurant.getName().trim().replaceAll("^\"|\"$", "")));
        saveFavoritesToCSV();
    }

    public void loadFavoritesFromCSV() {
        favorites.clear();
        File file = new File(FAVORITES_CSV_PATH);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String user = parts[0].trim();
                    String restaurantName = parts[1].replaceAll("\"", "").trim();
                    if (user.equals(username)) {
                        Restaurant r = findRestaurantByName(restaurantName);
                        if (r != null) {
                            boolean alreadyPresent = favorites.stream()
                                    .anyMatch(fav -> fav.getName() != null
                                            && fav.getName().equalsIgnoreCase(r.getName()));
                            if (!alreadyPresent) {
                                favorites.add(r);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Restaurant> getFavorites() {
        return favorites;
    }

    public void addBooking(Bookings b) {
        if (b != null) {
            bookings.add(b);
            saveBookingsToCSV();
        }
    }

    public void updateBooking(Bookings oldBooking, Bookings newBooking) {
        if (oldBooking != null && newBooking != null) {
            int index = bookings.indexOf(oldBooking);
            if (index != -1) {
                bookings.set(index, newBooking);
                saveBookingsToCSV();
            }
        }
    }

    public void deleteBooking(Bookings booking) {
        if (booking != null) {
            bookings.remove(booking);
            saveBookingsToCSV();
        }
    }

    public void addOwnedRestaurant(Restaurant r) {
        if (r != null) {
            ownedRestaurants.add(r);
        }
    }

    public java.util.List<Restaurant> getOwnedRestaurants() {
        return ownedRestaurants;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public Review getReview(String restaurantName) {
        return reviews.stream()
                .filter(r -> r.getRestaurantName().equals(restaurantName) && r.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public Restaurant findRestaurantByName(String name) {
        if (name == null) {
            return null;
        }
        String cleanName = name.trim().replaceAll("^\"|\"$", "");

        for (Restaurant r : allRestaurants) {
            String rName = r.getName() != null ? r.getName().trim() : "";
            if (rName.equalsIgnoreCase(cleanName)) {
                return r;
            }
        }

        for (Restaurant r : ownedRestaurants) {
            String rName = r.getName() != null ? r.getName().trim() : "";
            if (rName.equalsIgnoreCase(cleanName)) {
                return r;
            }
        }

        return null;
    }

    public void loadAllRestaurants() {
        allRestaurants.clear();
        String workingDir = System.getProperty("user.dir");

        // 1. Carica da restaurants_list.csv (dalla directory principale) prima per
        // ottenere l'ID massimo
        java.io.File restaurantsFile = new java.io.File(workingDir, "data/restaurants_list.csv");
        int maxExistingId = 0;
        if (restaurantsFile.exists()) {
            maxExistingId = getMaxRestaurantIdFromFile(restaurantsFile);
            loadUserRestaurants(restaurantsFile);
        }

        // 2. Carica da michelin_my_maps.csv (dalla directory principale) usando ID dopo
        // quelli esistenti
        java.io.File michelinFile = new java.io.File(workingDir, "data/michelin_my_maps.csv");
        if (michelinFile.exists()) {
            loadMichelinRestaurants(michelinFile, maxExistingId + 1);
        }
    }

    private int getMaxRestaurantIdFromFile(java.io.File file) {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length > 0) {
                    try {
                        int id = Integer.parseInt(fields[0].trim());
                        if (id > maxId)
                            maxId = id;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maxId;
    }

    private void loadMichelinRestaurants(java.io.File file, int startingId) {
        int idCounter = startingId;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length < 14) {
                    continue;
                }
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replaceAll("^\"|\"$", "").trim();
                }
                int id = idCounter++;
                String name = fields[0];
                String address = fields[1];
                String location = fields[2];
                String price = fields[3];
                String cuisine = fields[4];
                double rating = 0.0;
                int reviewsCount = 0;
                String phoneNumber = fields[7];
                String imageUrl = fields[8];
                String award = fields[10];
                String greenStar = fields[11];
                String facilities = fields[12];
                String description = fields[13];

                Restaurant r = new Restaurant(id, name, address, rating, reviewsCount, location, cuisine,
                        price, imageUrl, phoneNumber, award, greenStar, facilities, description, null);
                allRestaurants.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserRestaurants(java.io.File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (fields.length < 7)
                        continue;

                    int id = Integer.parseInt(fields[0].trim());
                    String username = fields[1].trim().replaceAll("^\"|\"$", "");
                    String name = capitalizeFirstLetter(fields[2].trim().replaceAll("^\"|\"$", ""));
                    String address = capitalizeFirstLetter(fields[3].trim().replaceAll("^\"|\"$", ""));
                    String location = capitalizeFirstLetter(fields[4].trim().replaceAll("^\"|\"$", ""));
                    String price = fields[5].trim().replaceAll("^\"|\"$", "");
                    String cuisine = capitalizeFirstLetter(fields[6].trim().replaceAll("^\"|\"$", ""));
                    String longitude = fields.length > 7 ? fields[7].trim().replaceAll("^\"|\"$", "") : "";
                    String latitude = fields.length > 8 ? fields[8].trim().replaceAll("^\"|\"$", "") : "";
                    String phoneNumber = fields.length > 9 ? fields[9].trim().replaceAll("^\"|\"$", "") : "";
                    String url = fields.length > 10 ? fields[10].trim().replaceAll("^\"|\"$", "") : "";
                    String websiteUrl = fields.length > 11 ? fields[11].trim().replaceAll("^\"|\"$", "") : "";
                    String award = fields.length > 12 ? fields[12].trim().replaceAll("^\"|\"$", "") : "";
                    String greenStar = fields.length > 13 ? fields[13].trim().replaceAll("^\"|\"$", "") : "";
                    String facilities = fields.length > 14 ? fields[14].trim().replaceAll("^\"|\"$", "") : "";
                    String description = fields.length > 15 ? fields[15].trim().replaceAll("^\"|\"$", "") : "";

                    Restaurant r = new Restaurant(id, name, address, 0.0, 0, location, cuisine, price,
                            "", phoneNumber, award, greenStar, facilities, description, username);
                    allRestaurants.add(r);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Restaurant> getAllRestaurants() {
        return allRestaurants;
    }

    public void addRestaurant(Restaurant restaurant) {
        if (restaurant == null)
            return;
        boolean alreadyPresent = allRestaurants.stream()
                .anyMatch(r -> r.getId() == restaurant.getId());
        if (!alreadyPresent) {
            allRestaurants.add(restaurant);
        }
    }

    public void removeRestaurant(Restaurant restaurant) {
        if (restaurant == null)
            return;
        allRestaurants.removeIf(r -> r.getName() != null && r.getName().equalsIgnoreCase(restaurant.getName()));
        saveRestaurantsToCSV();
    }

    private void saveRestaurantsToCSV() {
        File file = new File("data/michelin_my_maps.csv");
        List<String> lines = new ArrayList<>();
        lines.add(
                "\"name\",\"address\",\"location\",\"price\",\"cuisine\",\"longitude\",\"latitude\",\"phoneNumber\",\"award\",\"greenStar\",\"facilities\",\"description\"");
        for (Restaurant r : allRestaurants) {
            String line = String.format(
                    "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    r.getName(), r.getAddress(), r.getLocation(), r.getPrice(), r.getCuisine(),
                    "", "", r.getPhoneNumber(), r.getAward(), r.getGreenStar(), r.getFacilities(), r.getDescription());
            lines.add(line);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBookingsToCSV() {
        File file = new File(BOOKING_CSV_PATH);
        List<String> lines = new ArrayList<>();
        lines.add("username,restaurantName,date,time");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String user = parts[0];
                        if (!user.equals(username)) {
                            lines.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Bookings b : bookings) {
            String line = String.join(",", username, b.getRestaurantName(), b.getDate(), b.getTime());
            lines.add(line);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFavoritesToCSV() {
        File file = new File(FAVORITES_CSV_PATH);
        List<String> lines = new ArrayList<>();
        lines.add("username,restaurantName");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String user = parts[0];
                        if (!user.equals(username)) {
                            lines.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Restaurant r : favorites) {
            String line = username + "," + r.getName();
            lines.add(line);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOwnedRestaurantsFromCSV() {
        ownedRestaurants.clear();
        for (Restaurant restaurant : allRestaurants) {
            if (username != null && username.equals(restaurant.getOwner())) {
                ownedRestaurants.add(restaurant);
            }
        }
    }

    public void removeReview(Review review) {
        if (review != null) {
            reviews.remove(review);
            saveReviewsToCSV();
        }
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public boolean isRestaurantOwner(Restaurant restaurant) {
        if (restaurant == null || isNotLoggedIn()) {
            return false;
        }

        String role = getRole();
        if (!"owner".equalsIgnoreCase(role) && !"ristoratore".equalsIgnoreCase(role)) {
            return false;
        }

        String restaurantsListPath = "data/restaurants_list.csv";
        File file = new File(restaurantsListPath);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (fields.length < 3) {
                    continue;
                }

                try {
                    int restaurantId = Integer.parseInt(fields[0].trim());
                    String ownerUsername = fields[1].trim().replaceAll("^\"|\"$", "");
                    String restaurantName = fields[2].trim().replaceAll("^\"|\"$", "");

                    if (restaurant.getId() == restaurantId &&
                            username.equals(ownerUsername) &&
                            restaurant.getName().trim().replaceAll("^\"|\"$", "").equalsIgnoreCase(restaurantName)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addReply(Reply reply) {
        if (reply != null) {
            Reply existingReply = replies.stream()
                    .filter(r -> r.getOwnerUsername().equals(reply.getOwnerUsername()) &&
                            r.getReviewUsername().equals(reply.getReviewUsername()) &&
                            r.getRestaurantId() == reply.getRestaurantId())
                    .findFirst()
                    .orElse(null);

            if (existingReply != null) {
                replies.remove(existingReply);
            }

            replies.add(reply);
            saveRepliesToCSV();
        }
    }

    public void loadRepliesFromCSV() {
        replies.clear();
        File file = new File(REPLIES_CSV_PATH);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 4) {
                    continue;
                }
                String ownerUsername = parts[0].trim();
                String reviewUsername = parts[1].trim();
                int restaurantId;
                try {
                    restaurantId = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String replyText = parts[3].replaceAll("^\"|\"$", "").replace("\"\"", "\"");
                replies.add(new Reply(ownerUsername, reviewUsername, restaurantId, replyText));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRepliesToCSV() {
        File file = new File(REPLIES_CSV_PATH);
        List<String> lines = new ArrayList<>();
        lines.add("ownerUsername,reviewUsername,restaurantId,replyText");
        for (Reply reply : replies) {
            String line = String.format("%s,%s,%d,\"%s\"",
                    reply.getOwnerUsername(),
                    reply.getReviewUsername(),
                    reply.getRestaurantId(),
                    reply.getText().replace("\"", "\"\""));
            lines.add(line);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Reply getReplyForReview(String reviewUsername, int restaurantId) {
        return replies.stream()
                .filter(r -> r.getReviewUsername().equals(reviewUsername) && r.getRestaurantId() == restaurantId)
                .findFirst()
                .orElse(null);
    }

    public int getRestaurantIdByName(String name) {
        if (name == null) {
            return -1;
        }
        String cleanName = name.trim().replaceAll("^\"|\"$", "");
        for (Restaurant r : allRestaurants) {
            String rName = r.getName() != null ? r.getName().trim() : "";
            if (rName.equalsIgnoreCase(cleanName)) {
                return r.getId();
            }
        }
        return -1;
    }

    public void deleteReply(Reply reply) {
        if (reply != null) {
            replies.removeIf(r -> r.getOwnerUsername().equals(reply.getOwnerUsername()) &&
                    r.getReviewUsername().equals(reply.getReviewUsername()) &&
                    r.getRestaurantId() == reply.getRestaurantId());
            saveRepliesToCSV();
        }
    }

    public void updateReply(Reply oldReply, Reply newReply) {
        if (oldReply != null && newReply != null) {
            replies.removeIf(r -> r.getOwnerUsername().equals(oldReply.getOwnerUsername()) &&
                    r.getReviewUsername().equals(oldReply.getReviewUsername()) &&
                    r.getRestaurantId() == oldReply.getRestaurantId());
            replies.add(newReply);
            saveRepliesToCSV();
        }
    }

    private static InputStream getResourceStream(String path) throws IOException {
        InputStream stream = UserSession.class.getClassLoader().getResourceAsStream(path);
        if (stream != null) {
            return stream;
        }

        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            return new FileInputStream(file);
        }

        throw new IOException("Resource not found: " + path);
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
