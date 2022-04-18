package max51.com.vk.bookcrossing;

public class Elements {
    public String title;
    public String author;
    public String desk;
    public String uri;
    public String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Elements(String title, String author, String desk, String uri, String id) {
        this.title = title;
        this.author = author;
        this.desk = desk;
        this.uri = uri;
        this.id = id;
    }
}
