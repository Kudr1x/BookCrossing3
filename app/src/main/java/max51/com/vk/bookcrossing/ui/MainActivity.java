package max51.com.vk.bookcrossing.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import io.shubh.superiortoastlibrary.SuperiorToast;
import io.shubh.superiortoastlibrary.SuperiorToastWithHeadersPreDesigned;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.databinding.ActivityMainBinding;
import max51.com.vk.bookcrossing.ui.f3.Fragment3;

public class MainActivity extends AppCompatActivity {   //Активность для фрагментов главного интерфейса

    private ActivityMainBinding binding;
    public BottomNavigationView navView;
    private SharedPreferences sPref;    //Сохранение данных


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPref = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        Intent i = getIntent();
        boolean flag = i.getBooleanExtra("flag", false);
        View view = findViewById(android.R.id.content).getRootView();

        if(flag){
            SuperiorToast.makeSuperiorToast(getApplicationContext(), "Изменения сохраненны")
                    .setToastIcon(getResources().getDrawable(R.drawable.done))
                    .setColorToLeftVerticleStrip("#219BCC")
                    .showWithSimpleAnimation((ViewGroup) view, SuperiorToast.ANIMATION_SLIDE_LEFT_RIGHT_ENTRY_EXIT)
                    .show();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}