package max51.com.vk.bookcrossing.util.elements;

public class Elements {                   //Класс объявлений
    public String title;                  //Название
    public String author;                 //Автор
    public String desk;                   //Описание
    public String uri;                    //Фото
    public String id;                     //id создателя
    public String date;                   //Дата издетельства
    public String uploadId;               //id объявения
    public String profileName;            //Имя создателя
    public Boolean archived;              //Проверка на архивность
    private String city;                  //Город
    private String region;                //Регион

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchive(Boolean archive) {
        this.archived = archive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
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

    public Elements(){        //Конструктор по умолчанию

    }

    //Конструктор
    public Elements(String title, String author, String desk, String uri, String id, String uploadId, String profileName, Boolean archived, String date, String city, String region) {
        this.title = title;
        this.author = author;
        this.desk = desk;
        this.uri = uri;
        this.id = id;
        this.uploadId = uploadId;
        this.profileName = profileName;
        this.archived = archived;
        this.date = date;
        this.city = city;
        this.region = region;
    }
}