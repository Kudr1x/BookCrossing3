package max51.com.vk.bookcrossing.ui.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import max51.com.vk.bookcrossing.R;

public class Reset extends Fragment {

    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText email = view.findViewById(R.id.resetEmailText);
        Button button = view.findViewById(R.id.resetButton);
        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(view1 -> getActivity().onBackPressed());

        button.setOnClickListener(view1 -> {

            auth = FirebaseAuth.getInstance();
            String textEmail = email.getText().toString().trim();

            if(textEmail.isEmpty()){
                email.setError("Введите email");
                email.requestFocus();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                email.setError("Не рпавильный формат почты");
                email.requestFocus();
                return;
            }

            auth.sendPasswordResetEmail(textEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Проверьте свою почту для сброса пароля", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(), "Попробуйте ещё", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}