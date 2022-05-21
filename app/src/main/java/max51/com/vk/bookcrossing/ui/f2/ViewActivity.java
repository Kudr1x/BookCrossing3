package max51.com.vk.bookcrossing.ui.f2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.User;

public class ViewActivity extends AppCompatActivity {

    private String tg;
    private String vk;
    private String num;
    private String email;
    private String name;
    private String id;
    private ViewGroup viewGroup;
    private String fav;
    private String newFav;
    private String uploadId;
    private DatabaseReference mDatabaseRef;
    private CheckBox favorite;
    private Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        viewGroup = (ViewGroup) ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0);

        ImageView openInfo = findViewById(R.id.openInfo);
        ImageView back = findViewById(R.id.backBlack);
        favorite = findViewById(R.id.favorite);

        receiveData();

        start();

        openInfo.setOnClickListener(view -> dialogShow());

        back.setOnClickListener(view -> onBackPressed());

        favorite.setOnClickListener(view -> changeFavorite());
    }
    private void receiveData(){
        Intent i = getIntent();
        String title = i.getStringExtra("title");
        fav = i.getStringExtra("fav");
        String author = i.getStringExtra("author");
        String desk = i.getStringExtra("desk");
        String uri = i.getStringExtra("uri");
        id = i.getStringExtra("id");
        uploadId = i.getStringExtra("uploadId");

        TextView titleText = findViewById(R.id.viewTitle);
        TextView authorText = findViewById(R.id.viewAuthor);
        TextView deskText = findViewById(R.id.viewDesk);
        TextView nameView = findViewById(R.id.nameView);
        ImageView imageView = findViewById(R.id.viewImage);
        CircleImageView circleImageView = findViewById(R.id.circleImageView);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
        StorageReference fileReference = mStorageRef.child(id + "." + "jpeg");
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String urlProf = uri.toString();
                Picasso.get().load(urlProf).fit().centerCrop().into(circleImageView);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.id.equals(id)){
                        name = user.getName();
                        tg = user.getTg();
                        vk = user.getVk();
                        num = user.getNum();
                        email = user.getEmail();
                        nameView.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        titleText.setText(title);
        authorText.setText(author);
        deskText.setText(desk);
        nameView.setText(name);

        Picasso.get().load(uri).fit().centerCrop().into(imageView);
    }

    private void dialogShow(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_alert));
        dialog.show();

        TextView textTg = dialog.findViewById(R.id.tgView);
        TextView textVk = dialog.findViewById(R.id.vkView);
        TextView textNum = dialog.findViewById(R.id.numView);
        TextView textEmail = dialog.findViewById(R.id.emailView);

        ImageView tgCopy = dialog.findViewById(R.id.copyTg);
        ImageView vkCopy = dialog.findViewById(R.id.copyVk);
        ImageView numCopy = dialog.findViewById(R.id.copyNum);
        ImageView emailCopy = dialog.findViewById(R.id.copyEmail);

        textTg.setText(tg);
        textVk.setText(vk);
        textNum.setText(num);
        textEmail.setText(email);

        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        tgCopy.setOnClickListener(view -> {
            if(tg.isEmpty()){
                Snackbar.make(viewGroup, "Тут ничего нет", Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData myClip;
            myClip = ClipData.newPlainText("text", tg);
            myClipboard.setPrimaryClip(myClip);
            Snackbar.make(viewGroup, "Скопировано в буфер обмена", Snackbar.LENGTH_LONG).show();
        });

        vkCopy.setOnClickListener(view -> {
            if(vk.isEmpty()){
                Snackbar.make(viewGroup, "Тут ничего нет", Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData myClip;
            myClip = ClipData.newPlainText("text", vk);
            myClipboard.setPrimaryClip(myClip);
            Snackbar.make(viewGroup, "Скопировано в буфер обмена", Snackbar.LENGTH_LONG).show();
        });

        numCopy.setOnClickListener(view -> {
            if(num.isEmpty()){
                Snackbar.make(viewGroup, "Тут ничего нет", Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData myClip;
            myClip = ClipData.newPlainText("text", num);
            myClipboard.setPrimaryClip(myClip);
            Snackbar.make(viewGroup, "Скопировано в буфер обмена", Snackbar.LENGTH_LONG).show();
        });

        emailCopy.setOnClickListener(view -> {
            if(email.isEmpty()){
                Snackbar.make(viewGroup, "Тут ничего нет", Snackbar.LENGTH_LONG).show();
                return;
            }
            ClipData myClip;
            myClip = ClipData.newPlainText("text", email);
            myClipboard.setPrimaryClip(myClip);
            Snackbar.make(viewGroup, "Скопировано в буфер обмена", Snackbar.LENGTH_LONG).show();
        });
    }

    private void changeFavorite(){
        if(favorite.isChecked() == flag){
            Map<String, Object> hasMap = new HashMap<>();
            hasMap.put("favorite", newFav);
            mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
        }else{
            Map<String, Object> hasMap = new HashMap<>();
            hasMap.put("favorite", fav);
            mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
        }
    };

    private void start(){
        String[] separated = fav.split("\\|");
        if(Arrays.asList(separated).contains(uploadId)){
            favorite.setChecked(true);
            separated = ArrayUtils.removeAll(separated, uploadId);
            flag = false;
            newFav = String.join("|", separated);
        }else {
            newFav = fav + "|" + uploadId;
            flag = true;
        }
    }
}
