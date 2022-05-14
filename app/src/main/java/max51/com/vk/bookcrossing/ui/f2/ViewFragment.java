package max51.com.vk.bookcrossing.ui.f2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import max51.com.vk.bookcrossing.R;

public class ViewFragment extends Fragment {

    private String title;
    private String author;
    private String desk;
    private String uri;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleText = view.findViewById(R.id.viewTitle);
        TextView authorText = view.findViewById(R.id.viewAuthor);
        TextView deskText = view.findViewById(R.id.viewDesk);
        ImageView imageView = view.findViewById(R.id.viewImage);

        author = getArguments().getString("author");
        title = getArguments().getString("title");
        desk = getArguments().getString("desk");
        uri = getArguments().getString("uri");
        id = getArguments().getString("id");

        titleText.setText(title);
        authorText.setText(author);
        deskText.setText(desk);
        Picasso.get().load(uri).fit().centerCrop().into(imageView);
    }
}