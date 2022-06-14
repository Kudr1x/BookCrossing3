package max51.com.vk.bookcrossing.util.custom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationUtils {                       //Вспомогательный класс определяющий местоположение по долготе и широте
    @NonNull
    public static Location getMyLocation(AppCompatActivity context) {
        Location myLocation = new Location("");
        if((context != null) && ! (context.isFinishing())) {
            if(ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                myLocation = new Location("");
                ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else {
                GPSTracker gps = new GPSTracker(context);
                // Check if GPS enabled
                if(gps.canGetLocation()) {
                    myLocation = new Location("");//provider name is unnecessary
                    myLocation.setLatitude(gps.getLatitude());//your coords of course
                    myLocation.setLongitude(gps.getLongitude());
                    return myLocation;
                }
                else {
                    myLocation = new Location("");
                    gps.showSettingsAlert();
                }
            }
        }
        return myLocation;
    }
}
