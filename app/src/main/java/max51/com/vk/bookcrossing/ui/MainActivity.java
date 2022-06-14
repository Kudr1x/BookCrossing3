package max51.com.vk.bookcrossing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
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

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {   //Активность для фрагментов главного интерфейса

    private ActivityMainBinding binding;
    public BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = findViewById(android.R.id.content).getRootView();
        Intent i = getIntent();
        boolean flag = i.getBooleanExtra("flag", false);
        if(flag){
            Snackbar.make(view, "Изменения сохранены", Snackbar.LENGTH_LONG).show();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void setStatus(String status){
        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("status", status);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
    }

    //Смена статуса
    @Override
    protected void onResume() {
        super.onResume();
        setStatus("offline");
    }
}