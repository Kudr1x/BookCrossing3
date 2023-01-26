package max51.com.vk.bookcrossing.util.chats;

public class Messages {       //Класс сообщений
    private String message;   //Сообщение
    private String senderId;  //id отправител
    private long timeStamp;   //Время отправки

    public Messages() {  //Конструктор по умолчанию
    }

    public Messages(String message, String senderId, long timeStamp) {   //Конструктор сообщений
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
