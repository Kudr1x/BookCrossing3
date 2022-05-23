package max51.com.vk.bookcrossing.ui.f1;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;
import max51.com.vk.bookcrossing.ui.f2.ViewActivity;

public class EditActivity extends AppCompatActivity {

    private String title;
    private String author;
    private String desk;
    private String urif;
    private String id;
    private String uploadId;
    private EditText titleText;
    private EditText authorText;
    private TextView deskText;
    private ImageView imageView;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private String uri;
    private Uri image;
    private Boolean arch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit);

        titleText = findViewById(R.id.editTitle);
        authorText = findViewById(R.id.editAuthor);
        deskText = findViewById(R.id.editDesk);
        imageView = findViewById(R.id.editImage);
        ImageView btDel = findViewById(R.id.btDel);
        ImageView btSave = findViewById(R.id.btSave);
        ImageView btArch = findViewById(R.id.archive);

        Intent i = getIntent();

        author = i.getStringExtra("author");
        title = i.getStringExtra("title");
        desk = i.getStringExtra("desk");
        urif = i.getStringExtra("uri");
        id = i.getStringExtra("id");
        uploadId = i.getStringExtra("uploadId");
        arch = i.getBooleanExtra("archived", false);

        Drawable drawable = getResources().getDrawable(R.drawable.unarchive);

        if(arch) btArch.setImageDrawable(drawable);

        uri = urif;

        titleText.setText(title);
        authorText.setText(author);
        deskText.setText(desk);
        Picasso.get().load(urif).fit().centerCrop().into(imageView);

        imageView.setOnClickListener(view1 -> showChoicesDialog());

        btSave.setOnClickListener(view1 -> {
            save();
            Snackbar.make(view1, "Изменения сохраненны", Snackbar.LENGTH_LONG).show();
        });

        btDel.setOnClickListener(view1 -> showDelDialog());

        btArch.setOnClickListener(view -> showArhDialog());
    }

    private void showArhDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        TextView textView = dialog.findViewById(R.id.choosetxt);
        if(arch)textView.setText("Удалить из архива?");
        else textView.setText("Перенести в архив?");

        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);

        btYes.setOnClickListener(view -> {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
            Map<String, Object> hasMap = new HashMap<>();
            if(arch) hasMap.put("archived", false);
            else hasMap.put("archived", true);
            mDatabaseRef.child(uploadId).updateChildren(hasMap);
            dialog.cancel();
            Intent i = new Intent(EditActivity.this, MainActivity.class);
            EditActivity.this.startActivity(i);
        });

        btNo.setOnClickListener(view -> dialog.cancel());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void save() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("author", authorText.getText().toString());
        hasMap.put("title", titleText.getText().toString());
        hasMap.put("desk", deskText.getText().toString());
        hasMap.put("uri", urif);
        mDatabaseRef.child(uploadId).updateChildren(hasMap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = data.getData();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/");

        StorageReference fileReference = mStorageRef.child(urif.substring(87, 105));

        Picasso.get().load(image).into(imageView);

        fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urif = uri.toString();
                        save();
                    }
                });
            }
        });
    }

    private void showChoicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet2layout);

        Button btGall = dialog.findViewById(R.id.gallery);
        Button btCam = dialog.findViewById(R.id.cam);

        btGall.setOnClickListener(view -> {
            ImagePicker.with(this).galleryOnly().crop().start();
            dialog.cancel();
        });

        btCam.setOnClickListener(view -> {
            ImagePicker.with(this).cameraOnly().crop().start();
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDelDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);

        btYes.setOnClickListener(view -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("uploads").orderByChild("uploadId").equalTo(uploadId);

            FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(urif);

            photoRef.delete();

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        Intent i = new Intent(EditActivity.this, MainActivity.class);
                        EditActivity.this.startActivity(i);
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
