package max51.com.vk.bookcrossing.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.User;

public class Register extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText emailEditText = (EditText) getView().findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordEditText = (EditText) getView().findViewById(R.id.editTextTextPassword2);
        EditText nameEditText = (EditText) getView().findViewById(R.id.editTextTextPersonName);
        Button btReg = (Button) getView().findViewById(R.id.buttonRegister);
        mAuth = FirebaseAuth.getInstance();


        btReg.setOnClickListener(view1 -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();


            if(name.isEmpty()){
                nameEditText.setError("");
                nameEditText.requestFocus();
                return;
            }

            if(email.isEmpty()){
                emailEditText.setError("");
                emailEditText.requestFocus();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEditText.setError("");
                emailEditText.requestFocus();
                return;
            }

            if(password.isEmpty()){
                passwordEditText.setError("");
                passwordEditText.requestFocus();
                return;
            }

            if(password.length() < 6){
                passwordEditText.setError("");
                passwordEditText.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    User user = new User(name, email);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        });
    }
}