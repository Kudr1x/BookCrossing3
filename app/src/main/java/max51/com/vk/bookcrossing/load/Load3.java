package max51.com.vk.bookcrossing.load;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;

import com.squareup.picasso.Picasso;

import max51.com.vk.bookcrossing.R;

public class Load3 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = 1;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load3, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btLoad = view.findViewById(R.id.loadImage);
        Button btNext = view.findViewById(R.id.next3);
        ImageView imageView = view.findViewById(R.id.imageView);


        btLoad.setOnClickListener(view1 -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            //noinspection deprecation
            startActivityForResult(intent, 3);
        });

        btNext.setOnClickListener(view1 -> {

        });


    }
}