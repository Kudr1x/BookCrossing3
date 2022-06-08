package max51.com.vk.bookcrossing.ui.f2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;
import max51.com.vk.bookcrossing.ui.chats.ChatActivity;
import max51.com.vk.bookcrossing.ui.f1.EditActivity;
import max51.com.vk.bookcrossing.util.User;

public class ViewActivity extends AppCompatActivity {
    private String name;
    private String id;
    private String fav;
    private String newFav;
    private String date;
    private String uploadId;
    private DatabaseReference mDatabaseRef;
    private CheckBox favorite;
    private Boolean flag;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        ImageView openInfo = findViewById(R.id.openInfo);
        ImageView back = findViewById(R.id.backBlack);
        Button reportBt = findViewById(R.id.report);
        favorite = findViewById(R.id.favorite);

        view = getWindow().getDecorView().findViewById(android.R.id.content);

        receiveData();

        start();

        openInfo.setOnClickListener(view -> startChat());

        back.setOnClickListener(view -> onBackPressed());

        favorite.setOnClickListener(view -> changeFavorite());

        reportBt.setOnClickListener(view -> report());
    }

    private void report() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        TextView txt = dialog.findViewById(R.id.choosetxt);
        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);

        txt.setText("Подать жалобу?");

        btYes.setOnClickListener(view -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Report");
            databaseReference.child(uploadId).setValue(id);
            dialog.cancel();
        });

        btNo.setOnClickListener(view -> dialog.cancel());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void receiveData(){
        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String city = i.getStringExtra("city");
        String region = i.getStringExtra("region");
        fav = i.getStringExtra("fav");
        String author = i.getStringExtra("author");
        String desk = i.getStringExtra("desk");
        String uri = i.getStringExtra("uri");
        id = i.getStringExtra("id");
        uploadId = i.getStringExtra("uploadId");
        date = i.getStringExtra("date");

        TextView cityAndRegion = findViewById(R.id.cityAndRegion);
        TextView titleText = findViewById(R.id.viewTitle);
        TextView authorText = findViewById(R.id.viewAuthor);
        TextView deskText = findViewById(R.id.viewDesk);
        TextView nameView = findViewById(R.id.nameView);
        ImageView imageView = findViewById(R.id.viewImage);
        CircleImageView circleImageView = findViewById(R.id.circleImageView);

        cityAndRegion.setText(region + ", " + city);

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
                        nameView.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        titleText.setText(title + ", " + date);
        authorText.setText(author);
        deskText.setText(desk);
        nameView.setText(name);

        Picasso.get().load(uri).fit().centerCrop().into(imageView);
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
        System.out.println(fav);
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

    private void startChat(){
        Intent i = new Intent(ViewActivity.this, ChatActivity.class);
        i.putExtra("uid", id);
        i.putExtra("name", name);
        startActivity(i);
    }
}