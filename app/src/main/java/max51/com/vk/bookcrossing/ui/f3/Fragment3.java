package max51.com.vk.bookcrossing.ui.f3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;
import io.shubh.superiortoastlibrary.SuperiorToast;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.User;

public class Fragment3 extends Fragment {     //Профиль
    private Uri image;                        //Фото пользователя
    private CircleImageView imageProfile;     //Контейнер фото
    private TextView tvName;                  //Поле для имя
    private TextView tvEmail;                 //Поле для почты
    private Button btOut;                     //Кнопка выхода
    private FirebaseUser user;                //Пользователь
    private StorageReference mStorageRef;     //fireabse storage
    private String fav;                       //id любимых объявлений
    private SharedPreferences sPref;
    private BiometricManager biometricManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btOut = view.findViewById(R.id.btOut);
        tvEmail = view.findViewById(R.id.textEmail);
        tvName = view.findViewById(R.id.textName);
        imageProfile = view.findViewById(R.id.imageProfile);

        biometricManager = BiometricManager.from(getContext());

        ConstraintLayout changePassword = view.findViewById(R.id.changePassword);
        ConstraintLayout changeName = view.findViewById(R.id.changeName);
        ConstraintLayout changePhoto = view.findViewById(R.id.changePhoto);
        ConstraintLayout favoriteActivity = view.findViewById(R.id.favorite);
        ConstraintLayout info = view.findViewById(R.id.info);
        ConstraintLayout archived = view.findViewById(R.id.archived);
        ConstraintLayout city = view.findViewById(R.id.changeCity);
        ConstraintLayout keyImport = view.findViewById(R.id.keyImport);

        sPref = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
        user = FirebaseAuth.getInstance().getCurrentUser();

        tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        tvName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        getImage();

        //Просмотр архивных объявлений
        archived.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_navigation_notifications_to_archiveActivity));

        //Просмотр любимых объявлений
        favoriteActivity.setOnClickListener(view1 -> runFavoriteActivity());

        //Загрузка нового фото
        imageProfile.setOnClickListener(view1 -> showChoicesDialog());

        //Просмотр всех чатов
        info.setOnClickListener(view1 -> openDialogs());

        //Смена пароля
        changePassword.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_navigation_notifications_to_changePasswordActivity));

        //Смена имени
        changeName.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_navigation_notifications_to_changeNameActivity));

        //Смена фото
        changePhoto.setOnClickListener(view1 -> showChoicesDialog());

        //Смена города
        city.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_navigation_notifications_to_locationActivity));

        //Выход из аккаунта
        btOut.setOnClickListener(view1 -> showOutDialog());

        keyImport.setOnClickListener(view1 -> check());
    }

    private void openDialogs(){
        if(sPref.getString("privateKey", "").equals("")){
            viewToast();
        }else{
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_allChatsActivity);
        }
    }

    private void viewToast() {
        SuperiorToast.makeSuperiorToast(getContext(),
                        "Нет ключа шфирования")
                .setToastIcon(getResources().getDrawable(R.drawable.warning))
                .setColorToLeftVerticleStrip("#219BCC")
                .showWithSimpleAnimation((ViewGroup) getActivity().getWindow().getDecorView().getRootView() ,SuperiorToast.ANIMATION_SLIDE_BOTTOM_ENTRY_EXIT);
    }

    private void check() {
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            attemptBiometricAuth();
        } else {
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_QRActivity);
        }
    }

    private void attemptBiometricAuth() {
        Executor executor = ContextCompat.getMainExecutor(getContext());
        BiometricPrompt.AuthenticationCallback callback = getAuthenticationCallback();
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
        BiometricPrompt.PromptInfo promptInfo = getPromptInfo("Аутенфикация", "Пожалуйста, подтвердите свою личность", "", true);
        biometricPrompt.authenticate(promptInfo);
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                Snackbar.make(getView(), "Доступ получен", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_QRActivity);
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };
    }

    private BiometricPrompt.PromptInfo getPromptInfo(String title, String subtitle, String description, boolean isDeviceCredentialAllowed) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setDeviceCredentialAllowed(isDeviceCredentialAllowed)
                .build();
    }

    //СОхранение фото
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = data.getData();

        StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + "jpeg");
        fileReference.putFile(image);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(image).build();
        user.updateProfile(profileUpdates);

        Picasso.get().load(image).into(imageProfile);
    }

    //Выбор источника фото
    private void showChoicesDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet2layout);

        Button btGall = dialog.findViewById(R.id.gallery);
        Button btCam = dialog.findViewById(R.id.cam);

        //Фото из галереии
        btGall.setOnClickListener(view -> {
            ImagePicker.with(this).galleryOnly().crop().start();
            dialog.cancel();
        });

        //Фото с камеры
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

    //Диалог выхода
    private void showOutDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);
        TextView textView = dialog.findViewById(R.id.choosetxt);

        textView.setText("Выйти?");

        btYes.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            dialog.cancel();
            exit();
        });

        btNo.setOnClickListener(view -> dialog.cancel());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    //Просмотр избранных объявлений
    private void runFavoriteActivity() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        fav = user.getFavorite();
                        try {
                            Intent i = new Intent(getActivity().getBaseContext(), FavoriteActivity.class);
                            i.putExtra("fav", fav);
                            getActivity().startActivity(i);
                        }catch (Exception ignore){}
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //Выход из аккаунта
    private void exit(){
        FirebaseAuth.getInstance().signOut();
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_notifications_to_regActivity);
        getActivity().finish();
        SharedPreferences.Editor editor = sPref.edit();
        editor.clear();
        editor.apply();
    }

    //Загрузка фото пользователя
    private void getImage(){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
        StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + "jpeg");
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String urlProf = uri.toString();
                Picasso.get().load(urlProf).fit().centerCrop().into(imageProfile);
            }
        });
    }
}