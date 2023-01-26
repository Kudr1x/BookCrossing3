package max51.com.vk.bookcrossing.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {        //Класс для хранения информации о книге
    @SerializedName("volumeInfo")
    @Expose
    private VolumeInfo volumeInfo;

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

}
