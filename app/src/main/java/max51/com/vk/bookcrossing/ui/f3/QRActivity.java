package max51.com.vk.bookcrossing.ui.f3;

import static max51.com.vk.bookcrossing.util.encription.ECC.getKeyPair;
import static max51.com.vk.bookcrossing.util.encription.ECC.getPrivateKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.getPublicKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.sign;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PrivateKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PublicKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.verify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.prush.bndrsntchtimer.BndrsntchTimer;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.shubh.superiortoastlibrary.SuperiorToast;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;
import max51.com.vk.bookcrossing.util.elements.Elements;

public class QRActivity extends AppCompatActivity {

    private FirebaseUser user;
    private SharedPreferences sPref;
    private ImageView back;
    private ImageView key;
    private TextView txt;
    private Button showQrBt;
    private Button scanQrBt;
    private Button newKeyBt;
    private BndrsntchTimer timer;
    private String publicKey;
    private String privetKey;
    private String str;
    private ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);

        back = findViewById(R.id.back);
        info = findViewById(R.id.info);

        showQrBt = findViewById(R.id.showKey);
        scanQrBt = findViewById(R.id.setKey);
        newKeyBt = findViewById(R.id.newKey);

        timer = findViewById(R.id.timer);

        txt = findViewById(R.id.keyTxt);

        key = findViewById(R.id.keyImage);

        user = FirebaseAuth.getInstance().getCurrentUser();

        sPref = getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        publicKey = sPref.getString("publicKey", "");

        back.setOnClickListener(view -> onBackPressed());

        info.setOnClickListener(view -> getInfo());

        if(sPref.getString("privateKey", "") == ""){
            Drawable res = getResources().getDrawable(R.drawable.key_off_color);
            key.setImageDrawable(res);
            txt.setText("Нет ключа приватности");
            showQrBt.setEnabled(false);
        }else {
            Drawable res = getResources().getDrawable(R.drawable.key_color);
            key.setImageDrawable(res);
            txt.setText("Ключ указан верно");
            scanQrBt.setEnabled(false);
        }
        showQrBt.setOnClickListener(view -> showQR());

        scanQrBt.setOnClickListener(view -> scanQR());

        newKeyBt.setOnClickListener(view -> newKeyDialog());
    }

    private void getInfo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout2);

        ImageView close = dialog.findViewById(R.id.close);

        close.setOnClickListener(view -> {
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void scanQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Отсканируйте ключ");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @SuppressLint("ResourceAsColor")
    private void showQR() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            byte[] content = sign(FirebaseAuth.getInstance().getUid(), string2PrivateKey(sPref.getString("privateKey", "")));
            String value = Arrays.toString(content).replace("[", "")
                    .replace("]", "").trim() + "_" + sPref.getString("privateKey", "");
            Bitmap bitmap = barcodeEncoder.encodeBitmap(value, BarcodeFormat.QR_CODE, 350, 350);
            key.setImageBitmap(bitmap);
            timer.setProgressColorInt(R.color.colorAccent);
            getLifecycle().addObserver(timer.getLifecycleObserver());
            timer.start( 10000 );
            timer.setOnTimerElapsedListener( new BndrsntchTimer.OnTimerElapsedListener() {
                @Override
                public void onTimeElapsed( long elapsedDuration, long totalDuration ) {
                    if(elapsedDuration >= totalDuration ) {
                        Drawable res = getResources().getDrawable(R.drawable.key_color);
                        key.setImageDrawable(res);
                        timer.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) { ;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null) {
            if(result.getContents() != null) {
                str = result.getContents();
                checkKey();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void checkKey() {
        String[] arr = str.split("_");

        String[] temp = arr[0].split(",");

        byte[] content = new byte[temp.length];

        for (int i = 0; i < content.length; i++) {
            content[i] = Byte.parseByte(temp[i].replace(" ", ""));
        }
        try {
            if(verify(FirebaseAuth.getInstance().getUid(), content, string2PublicKey(publicKey))){
                sPref = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("privateKey", arr[1]);
                editor.apply();
                onBackPressed();
            }else {
                Toast.makeText(this, "Не верный ключ", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Попробуйте ещё раз", Toast.LENGTH_SHORT).show();
        }
    }

    private void newKeyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        Button btYes = dialog.findViewById(R.id.yes);
        Button btNo = dialog.findViewById(R.id.no);
        TextView textView = dialog.findViewById(R.id.choosetxt);

        textView.setText("Вы уверенны? Вы потеряете все имеющиеся диалоги");

        btYes.setOnClickListener(view -> {
            genNewKey();
            dialog.cancel();
            Intent i = new Intent(QRActivity.this, MainActivity.class);
            i.putExtra("flag", true);
            startActivity(i);
        });

        btNo.setOnClickListener(view -> dialog.cancel());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void genNewKey(){
        try {
            KeyPair keyPair=getKeyPair();
            publicKey = getPublicKey(keyPair);
            privetKey = getPrivateKey(keyPair);
            updateAllKey();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateAllKey(){
        keySave();
        saveOnUploads();
        saveOnUsers();
        deleteChats();
    }

    private void keySave(){
        sPref = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("publicKey", publicKey);
        editor.putString("privateKey", privetKey);
        editor.apply();
    }

    private void saveOnUsers(){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("publicKey", publicKey);
        mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
    }

    private void saveOnUploads(){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("key", publicKey);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    assert user != null;
                    if(Objects.equals(element.id, FirebaseAuth.getInstance().getUid())){
                        Map<String, Object> hasMap = new HashMap<>();
                        hasMap.put("key", publicKey);
                        mDatabaseRef.child(element.getUploadId()).updateChildren(hasMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteChats(){
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("chats");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    if(key.substring(0, 28).equals(FirebaseAuth.getInstance().getUid())){
                        mDatabaseRef.child(key).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}