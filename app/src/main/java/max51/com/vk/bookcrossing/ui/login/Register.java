package max51.com.vk.bookcrossing.ui.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.User;

public class Register extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText emailEditText = getView().findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordEditText = getView().findViewById(R.id.editTextTextPassword2);
        EditText nameEditText = getView().findViewById(R.id.editTextTextPersonName);
        ImageView back = getView().findViewById(R.id.back);
        Button btReg = getView().findViewById(R.id.buttonRegister);
        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(view1 -> getActivity().onBackPressed());

        btReg.setOnClickListener(view1 -> {
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

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    User user = new User(name, email, FirebaseAuth.getInstance().getCurrentUser().getUid(), "null", "null", "null", "start");
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
        });
    }
}