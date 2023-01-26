package max51.com.vk.bookcrossing.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import max51.com.vk.bookcrossing.R;

public class RegActivity extends AppCompatActivity {  //Активность для фрагментов регистрации, входа, сброса пароля

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
    }
}