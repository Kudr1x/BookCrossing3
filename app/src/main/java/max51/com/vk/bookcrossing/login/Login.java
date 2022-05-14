package max51.com.vk.bookcrossing.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import max51.com.vk.bookcrossing.R;

public class Login extends Fragment{

    private FirebaseAuth mAuth;
    private SharedPreferences sPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        sPref = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();

        String userEmail = sPref.getString("email", "");
        String userPassword = sPref.getString("password", "");

        if(null == mAuth.getCurrentUser()){
            userEmail = "";
            userPassword = "";
            editor.putString("email", "");
            editor.putString("password", "");
            editor.apply();
        }

        if(!userEmail.equals("")){
            mAuth.signInWithEmailAndPassword(userEmail, userPassword);
            Navigation.findNavController(view).navigate(R.id.action_login_to_mainActivity);
            System.out.println("Вход выполнен");
        }

        EditText emailEditText = getView().findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = getView().findViewById(R.id.editTextTextPassword);
        Button btLog = getView().findViewById(R.id.buttonLogin);


        TextView regText = getView().findViewById(R.id.reg);
        TextView restText = getView().findViewById(R.id.reset);

        restText.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_login_to_reset));

        regText.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_login_to_register));

        btLog.setOnClickListener(view12 -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

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
                passwordEditText.setError("Пароль должен быть больше 6 сомволов");
                passwordEditText.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();
                        Navigation.findNavController(view12).navigate(R.id.action_login_to_mainActivity);
                    }else {
                        Snackbar.make(view12, "Вы не подтвердили свою почту", Snackbar.LENGTH_LONG).show();
                    }
                }else {
                    Snackbar.make(view12, "Ошибка входа. Проверьте данные", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}