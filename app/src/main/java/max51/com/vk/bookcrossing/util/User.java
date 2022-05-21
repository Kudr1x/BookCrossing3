package max51.com.vk.bookcrossing.util;

public class User {

    public String name;
    public String email;
    public String id;
    public String tg;
    public String vk;
    public String num;
    public String favorite;

    public User(){ }


    public User(String name, String email, String id, String tg, String vk, String num, String favorite) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.tg = tg;
        this.vk = vk;
        this.num = num;
        this.favorite = favorite;
    }

    public String getId() { return id; }

    public String getFavorite() {return favorite;}

    public void setFavorite(String favorite) {this.favorite = favorite;}

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
