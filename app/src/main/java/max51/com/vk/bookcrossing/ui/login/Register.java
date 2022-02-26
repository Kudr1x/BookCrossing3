package max51.com.vk.bookcrossing.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.User;

public class Register extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EditText emailEditText = (EditText) getView().findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordEditText = (EditText) getView().findViewById(R.id.editTextTextPassword2);
        EditText nameEditText = (EditText) getView().findViewById(R.id.editTextTextPersonName);
        Button btReg = (Button) getView().findViewById(R.id.buttonRegister);
        mAuth = FirebaseAuth.getInstance();


        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity().getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                                        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_navigation_dashboard);
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(), "hz", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(), "hz", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }
}