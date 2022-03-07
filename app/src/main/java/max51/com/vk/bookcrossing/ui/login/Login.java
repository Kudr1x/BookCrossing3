package max51.com.vk.bookcrossing.ui.login;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText emailEditText = (EditText) getView().findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = (EditText) getView().findViewById(R.id.editTextTextPassword);
        Button btLog = (Button) getView().findViewById(R.id.buttonLogin);

        mAuth = FirebaseAuth.getInstance();

        TextView regText = (TextView) getView().findViewById(R.id.reg);

        regText.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_loginFragment_to_registerFragment));

        btLog.setOnClickListener(view12 -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

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

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Navigation.findNavController(view12).navigate(R.id.action_loginFragment_to_navigation_dashboard);
                }else {
                    Snackbar.make(view12, "Ошибка входа. Проверьте данные", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}