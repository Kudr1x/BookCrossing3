package max51.com.vk.bookcrossing.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VolumeInfo implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("authors")
    @Expose
    private List<String> authors = null;


    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }
}
