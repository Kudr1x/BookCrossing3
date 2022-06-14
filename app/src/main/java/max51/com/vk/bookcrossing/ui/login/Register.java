package max51.com.vk.bookcrossing.ui.login;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import max51.com.vk.bookcrossing.ui.f3.LocationActivity;
import max51.com.vk.bookcrossing.util.User;
import max51.com.vk.bookcrossing.util.city.City;
import max51.com.vk.bookcrossing.util.city.CityAdapter;
import max51.com.vk.bookcrossing.util.custom.LocationUtils;

public class Register extends Fragment {      //Фрагмент регистрации

    private ArrayList<City> arrayList = new ArrayList<>();       //Массив для подсказок
    private ArrayList<String> onlyCity = new ArrayList<>();      //Массив городов
    private ArrayList<String> onlyRegion = new ArrayList<>();    //Массив регионов
    private AutoCompleteTextView editText;                       //Подсказки
    private String city;                                         //Город
    private String region;                                       //Регион
    private EditText emailEditText;                              //Ввод почты
    private EditText passwordEditText;                           //Ввод пароля
    private EditText nameEditText;                               //Ввод имени

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emailEditText = getView().findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = getView().findViewById(R.id.editTextTextPassword2);
        nameEditText = getView().findViewById(R.id.editTextTextPersonName);
        ImageView back = getView().findViewById(R.id.back);
        Button btReg = getView().findViewById(R.id.buttonRegister);
        editText = getView().findViewById(R.id.autoComp);
        ImageView gps = getView().findViewById(R.id.gps);

        back.setOnClickListener(view1 -> getActivity().onBackPressed());

        parser(loadJSONFromAsset());

        editText = getView().findViewById(R.id.autoComp);
        CityAdapter adapter = new CityAdapter(getContext(), arrayList);
        editText.setAdapter(adapter);

        System.out.println(arrayList.size());

        gps.setOnClickListener(view1 -> getCityGps());

        btReg.setOnClickListener(view1 -> getRegData());
    }

    //Поиск города по gps
    private void getCityGps() {
        Location location = LocationUtils.getMyLocation((AppCompatActivity) getContext());
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
            }else {
                Snackbar.make(getView(), "Ошибка", Snackbar.LENGTH_LONG).show();
            }
        }catch (IOException e) {
            Snackbar.make(getView(), "Ошибка", Snackbar.LENGTH_LONG).show();
        }
    }

    //Преобразование json
    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("russia.json");
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

    //парсинг json
    private void parser(String jsonString) {
        try {
            JSONArray obj = new JSONArray(jsonString);
            for(int i = 0; i < obj.length(); i++){
                JSONObject t = obj.getJSONObject(i);
                onlyCity.add(t.getString("city"));
                onlyRegion.add(t.getString("region"));
                arrayList.add(new City(t.getString("city"), t.getString("region")));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Проверка введёных данных
    private void getRegData(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();


        if(name.isEmpty()){
            nameEditText.setError("Введите имя");
            nameEditText.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailEditText.setError("Введите email");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Не правильный формат почты");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordEditText.setError("Введите пароль");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordEditText.setError("Пароль должен быть больше 6 символо");
            passwordEditText.requestFocus();
            return;
        }

        if(!onlyCity.contains(editText.getText().toString())){
            editText.setError("Город не найдён");
            editText.requestFocus();
            return;
        }else{
            city = editText.getText().toString();
            region = onlyRegion.get(onlyCity.indexOf(city));
        }

        createUser(email, password, name);
    }

    //Создание пользователя
    private void createUser(String email, String password, String name){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User user = new User(name, FirebaseAuth.getInstance().getCurrentUser().getUid(), "start", city, region, "offline");
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates);
                        firebaseUser.sendEmailVerification();
                        Snackbar.make(getView(), "Подтвердите электронную почту для завершения регистрации", Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(getView(), "Ошибка", Snackbar.LENGTH_LONG).show();
                    }
                });
            }else {
                Snackbar.make(getView(), "Ошибка", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}