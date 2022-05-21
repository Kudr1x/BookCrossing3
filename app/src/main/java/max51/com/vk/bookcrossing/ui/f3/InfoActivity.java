package max51.com.vk.bookcrossing.ui.f3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.User;

public class InfoActivity extends AppCompatActivity {

    private String tgText;
    private String vkText;
    private String numText;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        EditText tg = findViewById(R.id.tg);
        EditText vk = findViewById(R.id.vk);
        EditText num = findViewById(R.id.num);
        Button save = findViewById(R.id.save);
        ImageView back = findViewById(R.id.back);

        String cid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    System.out.println(user.getId() + " " + cid);
                    if(user.id.equals(cid)){
                        if(!user.getTg().equals("null"))tg.setText(user.getTg());
                        if(!user.getVk().equals("null"))vk.setText(user.getVk());
                        if(!user.getNum().equals("null"))num.setText(user.getNum());

                        System.out.println(user.getTg() + " " + user.getVk() + " " + user.getNum() + "\n");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(get -> {
            DatabaseReference hopperRef = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> hopperUpdates = new HashMap<>();
            hopperUpdates.put("tg", tg.getText().toString());
            hopperUpdates.put("vk", vk.getText().toString());
            hopperUpdates.put("num", num.getText().toString());
            hopperRef.updateChildren(hopperUpdates);
        });

        back.setOnClickListener(view -> onBackPressed());
    }
}