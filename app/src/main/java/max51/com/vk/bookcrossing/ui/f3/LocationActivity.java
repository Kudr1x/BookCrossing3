package max51.com.vk.bookcrossing.ui.f3;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.city.City;
import max51.com.vk.bookcrossing.util.city.CityAdapter;
import max51.com.vk.bookcrossing.util.custom.LocationUtils;

public class LocationActivity extends AppCompatActivity {

    private ArrayList<City> arrayList = new ArrayList<>();
    private ArrayList<String> onlyCity = new ArrayList<>();
    private ArrayList<String> onlyRegion = new ArrayList<>();
    private View view;
    private AutoCompleteTextView editText;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String jsonString = loadJSONFromAsset();
        parser(jsonString);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        view = getWindow().getDecorView().findViewById(android.R.id.content);

        editText = findViewById(R.id.autoComp);
        CityAdapter adapter = new CityAdapter(this, arrayList);
        editText.setAdapter(adapter);

        ImageView back = findViewById(R.id.back);

        Button button = findViewById(R.id.saveLoc);

        ImageView gps = findViewById(R.id.gps);

        button.setOnClickListener(view -> saveCity(editText.getText().toString().trim()));

        back.setOnClickListener(view -> onBackPressed());
        
        gps.setOnClickListener(view -> getCityGps());
    }

    private void getCityGps() {
        Location location = LocationUtils.getMyLocation(LocationActivity.this);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            if(onlyCity.contains(city)){
                editText.setText(city);
                saveCity(city);
            }else {
                Snackbar.make(view, "Ошибка", Snackbar.LENGTH_LONG).show();
            }
        }catch (IOException e) {
            Snackbar.make(view, "Ошибка", Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveCity(String currentCity) {
        if(onlyCity.contains(currentCity)){
            Snackbar.make(view, "Изменения сохраненны", Snackbar.LENGTH_LONG).show();
            Map<String, Object> hasMap = new HashMap<>();
            hasMap.put("city", currentCity);
            hasMap.put("region", onlyRegion.get(onlyCity.indexOf(currentCity)));
            mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
        }else {
            Snackbar.make(view, "Ошибка", Snackbar.LENGTH_LONG).show();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("russia.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void parser(String jsonString) {
        try {
            JSONArray obj = new JSONArray(jsonString);
            for(int i = 0; i < obj.length(); i++){
                JSONObject t = obj.getJSONObject(i);
                arrayList.add(new City(t.getString("city"), t.getString("region")));
                onlyCity.add(t.getString("city"));
                onlyRegion.add(t.getString("region"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}