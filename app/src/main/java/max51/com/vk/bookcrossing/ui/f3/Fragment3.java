package max51.com.vk.bookcrossing.ui.f3;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import max51.com.vk.bookcrossing.R;

public class Fragment3 extends Fragment {

    private Uri image;
    private CircleImageView imageProfile;
    private TextView tvName;
    private TextView tvEmail;
    private Button btOut;
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btOut = view.findViewById(R.id.button);
        tvEmail = view.findViewById(R.id.textEmail);
        tvName = view.findViewById(R.id.textName);
        imageProfile = view.findViewById(R.id.imageProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        tvEmail.setText(user.getEmail());
        tvName.setText(user.getDisplayName());

        Uri userImage = user.getPhotoUrl();

        if(userImage != null) Picasso.get().load(userImage).into(imageProfile);

        imageProfile.setOnClickListener(view1 -> showChoicesDialog());

        btOut.setOnClickListener(View -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(view).navigate(R.id.action_navigation_notifications_to_regActivity);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = data.getData();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(image).build();
        user.updateProfile(profileUpdates);

        Picasso.get().load(image).into(imageProfile);
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
}