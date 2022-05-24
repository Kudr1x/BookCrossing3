package max51.com.vk.bookcrossing.ui.f3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;
import max51.com.vk.bookcrossing.ui.f1.Fragment1;

public class changePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText ed1 = findViewById(R.id.editTextPassword1);
        EditText ed2 = findViewById(R.id.editTextPassword2);
        EditText ed3 = findViewById(R.id.editTextPassword3);

        Button bt = findViewById(R.id.changePas);
        ImageView im = findViewById(R.id.back);

        bt.setOnClickListener(view -> {
            if(ed1.getText().toString().isEmpty()){
                ed1.setError("Введите пароль");
                ed1.requestFocus();
                return;
            }

            if(ed2.getText().toString().isEmpty()){
                ed2.setError("Введите новый пароль");
                ed2.requestFocus();
                return;
            }

            if(ed3.getText().toString().isEmpty()){
                ed3.setError("Повторите новый пароль");
                ed3.requestFocus();
                return;
            }

            String oldPsw = ed1.getText().toString();

            AuthCredential authCredential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), oldPsw);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if(!ed2.getText().toString().equals(ed3.getText().toString())) return;
                    else FirebaseAuth.getInstance().getCurrentUser().updatePassword(ed2.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent i = new Intent(changePasswordActivity.this, MainActivity.class);
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
                    ed1.setError("Неправильный пароль");
                }
            });
        });

        im.setOnClickListener(view -> onBackPressed());
    }
}