package max51.com.vk.bookcrossing.util.chats;

public class ChatUserItem {     //Класс диалогов
    String id;                  //id собеседниа
    String uri;                 //Фото пользователя
    String lastMsg;             //Последнее сообщение
    String name;                //Имя собеседника
    Boolean flag;               //Переменная отвечающая за выбор конструктора

    public ChatUserItem(String id, String lastMsg, Boolean flag) {  //1 конструктор
        this.id = id;
        this.lastMsg = lastMsg;
    }

    public ChatUserItem(String id, String name) {          //2 конструктор
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
