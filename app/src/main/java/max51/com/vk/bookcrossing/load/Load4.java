package max51.com.vk.bookcrossing.load;

import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import max51.com.vk.bookcrossing.Elements;
import max51.com.vk.bookcrossing.R;

public class Load4 extends Fragment {

    private Uri image;
    private String title;
    private String author;
    private String desk;
    private String id;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button chose = view.findViewById(R.id.choose);
        Button next = view.findViewById(R.id.next4);
        ImageView imageView = view.findViewById(R.id.imageView);
        ProgressBar mProgressBar = view.findViewById(R.id.progressBar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        author = getArguments().getString("author");
        title = getArguments().getString("title");
        desk = getArguments().getString("desk");
        id = FirebaseAuth.getInstance().getUid();

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                image = result;
                imageView.setImageURI(result);
            }
        });

        chose.setOnClickListener(view1 -> launcher.launch("image/*"));

        next.setOnClickListener(view1 -> {
            if(image != null){
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + "png");

                fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 5000);


                        Toast.makeText(getContext(), "Успешно", Toast.LENGTH_LONG).show();
                        Elements elements = new Elements(title, author, desk, taskSnapshot.getUploadSessionUri().toString(), id);

                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(elements);

                        Navigation.findNavController(view).navigate(R.id.action_load4_to_mainActivity2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double Progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) Progress);
                    }
                });
            }else{
                Toast.makeText(getContext(), "Выберете файл", Toast.LENGTH_LONG).show();
            }
        });
    }
}