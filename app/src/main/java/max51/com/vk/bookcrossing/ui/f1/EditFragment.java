package max51.com.vk.bookcrossing.ui.f1;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import max51.com.vk.bookcrossing.R;

public class EditFragment extends Fragment{

    private String title;
    private String author;
    private String desk;
    private String uri;
    private String id;
    private String uploadId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText titleText = view.findViewById(R.id.editTitle);
        EditText authorText = view.findViewById(R.id.editAuthor);
        TextView deskText = view.findViewById(R.id.editDesk);
        ImageView imageView = view.findViewById(R.id.editImage);
        ImageView btDel = view.findViewById(R.id.btDel);

        author = getArguments().getString("author");
        title = getArguments().getString("title");
        desk = getArguments().getString("desk");
        uri = getArguments().getString("uri");
        id = getArguments().getString("id");
        uploadId = getArguments().getString("uploadId");

        titleText.setText(title);
        authorText.setText(author);
        deskText.setText(desk);
        Picasso.get().load(uri).fit().centerCrop().into(imageView);

        btDel.setOnClickListener(view1 -> showDelDialog());
    }

    private void showDelDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);

        btYes.setOnClickListener(view -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("uploads").orderByChild("uploadId").equalTo(uploadId);

            FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(uri);

            photoRef.delete();

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        Navigation.findNavController(getView()).navigate(R.id.action_editFragment_to_navigation_home);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.toString());
                }
            });

            dialog.cancel();
        });

        btNo.setOnClickListener(view -> dialog.cancel());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
