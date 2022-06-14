package max51.com.vk.bookcrossing.ui.f3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;

public class changeNameActivity extends AppCompatActivity {  //Имзенение имени пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        EditText checkPsw = findViewById(R.id.checkPsw);
        EditText newName = findViewById(R.id.newName);
        ImageView back = findViewById(R.id.back);
        Button bt = findViewById(R.id.changeNm);



        bt.setOnClickListener(view -> {
            if(checkPsw.getText().toString().isEmpty()){
                checkPsw.setError("Введите пароль");
                checkPsw.requestFocus();
                return;
            }

            if(newName.getText().toString().isEmpty()){
                newName.setError("Введите новое имя");
                newName.requestFocus();
                return;
            }

            String psw = checkPsw.getText().toString();

            //Проверка пароля и смена имени
            AuthCredential authCredential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), psw);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newName.getText().toString()).build();
                    firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent i = new Intent(changeNameActivity.this, MainActivity.class);
                            i.putExtra("flag", true);
                            startActivity(i);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(view, "Ошибка", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    checkPsw.setError("Неправильный пароль");
                }
            });
        });

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());
    }
}