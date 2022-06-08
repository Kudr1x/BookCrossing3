package max51.com.vk.bookcrossing.util.city;

public class City {
    String city;
    String region;

    public City(){

    }

    public City(String city, String region) {
        this.city = city;
        this.region = region;
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
}
