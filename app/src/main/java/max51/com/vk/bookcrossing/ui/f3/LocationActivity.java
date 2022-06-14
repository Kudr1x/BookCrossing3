package max51.com.vk.bookcrossing.ui.f3;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

public class LocationActivity extends AppCompatActivity {      //Смена города

    private ArrayList<City> arrayList = new ArrayList<>();     //Массив для подсказок
    private ArrayList<String> onlyCity = new ArrayList<>();    //Массив городов
    private ArrayList<String> onlyRegion = new ArrayList<>();  //Массив регионов
    private View view;                                         //view
    private AutoCompleteTextView editText;                     //Поле для подсказок
    private DatabaseReference mDatabaseRef;                    //бд

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

        //Сохранение города
        button.setOnClickListener(view -> saveCity(editText.getText().toString().trim()));

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());

        //Поиск города по gps
        gps.setOnClickListener(view -> getCityGps());
    }

    //Поиск города по gps
    private void getCityGps() {
        Location location = LocationUtils.getMyLocation(LocationActivity.this);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            double x = location.getLatitude();
            double y = location.getLongitude();
            if(Build.VERSION.SDK_INT < 33) {
                addresses = geocoder.getFromLocation(x, y, 1);
            }
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

    //Провекра города и сохранение
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

    //Перевод json в строку
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

    //Парсинг
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