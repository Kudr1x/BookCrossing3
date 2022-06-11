package max51.com.vk.bookcrossing.util.chats;

public class ChatUserItem {
    String id;
    String uri;
    String lastMsg;
    String name;
    Boolean flag;

    public ChatUserItem(String id, String lastMsg, Boolean flag) {
        this.id = id;
        this.lastMsg = lastMsg;
    }

    public ChatUserItem(String id, String name) {
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
