package max51.com.vk.bookcrossing.util;

public class User {      //Класс пользователя

    public String name;         //Имя
    public String id;           //id
    public String favorite;     //Любимые объявления
    public String city;         //Город
    public String region;       //Регион
    public String status;       //Статус онлайн или оффлайн
    public String publicKey;       //Статус онлайн или оффлайн

    public User(){ }             //Конструктор по умолчанию

    //Конструктор
    public User(String name, String id, String favorite, String city, String region, String status, String publicKey) {
        this.name = name;
        this.id = id;
        this.favorite = favorite;
        this.city = city;
        this.region = region;
        this.status = "offline";
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
