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

public class Load2 extends Fragment {

    String author;
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button bt = view.findViewById(R.id.next2);
        EditText ed = view.findViewById(R.id.editText);

        author = getArguments().getString("author");
        title = getArguments().getString("title");
        if(!author.isEmpty()) ed.setText(author);

        bt.setOnClickListener(view1 -> {
            author = ed.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("author", author);
            bundle.putString("title", title);

            if(author.isEmpty()){
                ed.setError("Укажите автора");
                ed.requestFocus();
                return;
            }
            Navigation.findNavController(view).navigate(R.id.action_load2_to_load3, bundle);
        });
    }
}