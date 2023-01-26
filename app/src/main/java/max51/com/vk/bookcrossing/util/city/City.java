package max51.com.vk.bookcrossing.util.city;

public class City {      //Класс для подсказок при выборе города
    private String city;        //Город
    private String region;      //Регион


    public City(String city, String region) {     //Конструктор
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
