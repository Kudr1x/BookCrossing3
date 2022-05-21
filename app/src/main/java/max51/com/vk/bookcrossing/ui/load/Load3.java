package max51.com.vk.bookcrossing.ui.load;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import max51.com.vk.bookcrossing.R;

public class Load3 extends Fragment {

    private String title;
    private String author;
    private String desk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btNext = view.findViewById(R.id.next3);
        EditText editText = view.findViewById(R.id.editTextTextMultiLine2);

        author = getArguments().getString("author");
        title = getArguments().getString("title");

        btNext.setOnClickListener(view1 -> {

            desk = editText.getText().toString();

            if(desk.isEmpty()){
                editText.setError("Добавьте описание");
                editText.requestFocus();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("author", author);
            bundle.putString("title", title);
            bundle.putString("desk", desk);
            Navigation.findNavController(view).navigate(R.id.action_load3_to_load4, bundle);
        });
    }
}