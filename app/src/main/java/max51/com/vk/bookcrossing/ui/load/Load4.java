package max51.com.vk.bookcrossing.ui.load;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import max51.com.vk.bookcrossing.util.Elements;
import max51.com.vk.bookcrossing.R;

public class Load4 extends Fragment {

    private Uri image;
    private String title;
    private String author;
    private String desk;
    private String id;

    private ImageView imageView;
    private Button chose;
    private Button next;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chose = view.findViewById(R.id.choose);
        next = view.findViewById(R.id.next4);
        imageView = view.findViewById(R.id.imageView);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        author = getArguments().getString("author");
        title = getArguments().getString("title");
        desk = getArguments().getString("desk");
        id = FirebaseAuth.getInstance().getUid();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Загружается....");
        progressDialog.setCancelable(false);

        chose.setOnClickListener(view1 -> showChoicesDialog());

        next.setOnClickListener(view1 -> createPost());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        image = data.getData();
        Picasso.get().load(image).into(imageView);
    }

    private void showChoicesDialog() {
        final Dialog dialog = new Dialog(getContext());
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

    private void createPost(){
        if(image != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + "jpeg");
            fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            String uploadId = mDatabaseRef.push().getKey();
                            Elements elements = new Elements(title, author, desk, url, id, uploadId, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), false);
                            mDatabaseRef.child(uploadId).setValue(elements);
                        }
                    });

                    Navigation.findNavController(getView()).navigate(R.id.action_load4_to_mainActivity2);
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressDialog.show();
                }
            });
        }else{
            Toast.makeText(getContext(), "Выберете файл", Toast.LENGTH_LONG).show();
        }
    }
}