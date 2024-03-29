package max51.com.vk.bookcrossing.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f3.FavoriteActivity;
import max51.com.vk.bookcrossing.util.User;

public class Login extends Fragment{  //Вход в аккаунт по почте и паролю

    private FirebaseAuth mAuth;         //firebase авторизация
    private SharedPreferences sPref;    //Сохранение данных

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        }

        EditText emailEditText = getView().findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = getView().findViewById(R.id.editTextTextPassword);
        Button btLog = getView().findViewById(R.id.buttonLogin);

        TextView regText = getView().findViewById(R.id.reg);
        TextView restText = getView().findViewById(R.id.reset);

        //Переход к востановлению пароля
        restText.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_login_to_reset));

        //Переход к регистрации
        regText.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_login_to_register));

        //Проверка вданных
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

            //Вход
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

    private void getKey() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString("publicKey", user.getPublicKey());
                        Toast.makeText(getContext(), user.getPublicKey(), Toast.LENGTH_SHORT).show();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}