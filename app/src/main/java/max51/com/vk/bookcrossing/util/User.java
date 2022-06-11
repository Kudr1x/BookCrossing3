package max51.com.vk.bookcrossing.util;

public class User {

    public String name;
    public String id;
    public String favorite;
    public String city;
    public String region;
    public String status;

    public User(){ }

    public User(String name, String id, String favorite, String city, String region, String status) {
        this.name = name;
        this.id = id;
        this.favorite = favorite;
        this.city = city;
        this.region = region;
        this.status = "offline";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getId() { return id; }

    public String getFavorite() {return favorite;}

    public void setFavorite(String favorite) {this.favorite = favorite;}

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
