package max51.com.vk.bookcrossing.ui.f1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.shubh.superiortoastlibrary.SuperiorToast;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.MainActivity;

public class EditActivity extends AppCompatActivity {    //Изменение собственных объявлений

    private String title;                   //Заголовок
    private String author;                  //Автор
    private String desk;                    //Описание
    private String urif;                    //Фото из объявления
    private String id;                      //id Пользователя
    private String date;                    //Год издания
    private String uploadId;                //id
    private String y;                       //Текущий год
    private String m;                       //Текущий месяц
    private Uri image;                      //Фото
    private Boolean arch;                   //Проверка на архивность

    private EditText titleText;             //Контейнер заголовка
    private EditText authorText;            //Контейнер автора
    private EditText dateText;              //Контейнер даты
    private TextView deskText;              //Контейнер описания
    private ImageView imageView;            //Контейнер фото
    private DatabaseReference mDatabaseRef; //База данных Realtime
    private StorageReference mStorageRef;   //Storage

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit);

        titleText = findViewById(R.id.editTitle);
        authorText = findViewById(R.id.editAuthor);
        deskText = findViewById(R.id.editDesk);
        dateText = findViewById(R.id.editDate);
        imageView = findViewById(R.id.editImage);
        ImageView btDel = findViewById(R.id.btDel);
        ImageView btSave = findViewById(R.id.btSave);
        ImageView btArch = findViewById(R.id.archive);
        ImageView calendar = findViewById(R.id.calendarEdit);

        Drawable drawable = getResources().getDrawable(R.drawable.unarchive);

        getData();

        //Проверя на архивность
        if(arch) btArch.setImageDrawable(drawable);

        //Берём теущий текущую дату
        String timeStamp = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
        y = timeStamp.substring(0,4);
        m = timeStamp.substring(4,6);

        //Заполняем все поля и фото
        titleText.setText(title);
        authorText.setText(author);
        deskText.setText(desk);
        Picasso.get().load(urif).fit().centerCrop().into(imageView);
        textWatcher();

        dateText.setText(date.substring(0,2) + date.substring(3,7));

        //Смена фотографии
        imageView.setOnClickListener(view1 -> showChoicesDialog());

        //Применить сохранение
        btSave.setOnClickListener(view1 -> {
            save();
            if(checkDate()) {
                SuperiorToast.makeSuperiorToast(getApplicationContext(),
                                "Изменения сохраненны")
                        .setToastIcon(getResources().getDrawable(R.drawable.save_color))
                        .setColorToLeftVerticleStrip("#219BCC")
                        .showWithSimpleAnimation((ViewGroup) getWindow().getDecorView().getRootView() , SuperiorToast.ANIMATION_SLIDE_BOTTOM_ENTRY_EXIT);
            }
            else {
                SuperiorToast.makeSuperiorToast(getApplicationContext(),
                                "Не верные данные")
                        .setToastIcon(getResources().getDrawable(R.drawable.warning))
                        .setColorToLeftVerticleStrip("#219BCC")
                        .showWithSimpleAnimation((ViewGroup) getWindow().getDecorView().getRootView() , SuperiorToast.ANIMATION_SLIDE_BOTTOM_ENTRY_EXIT);
            }
        });


        //Удалить объявление
        btDel.setOnClickListener(view1 -> showDelDialog());

        //Убрать изобъявление из общего доступа (перенести в архив)
        btArch.setOnClickListener(view -> showArhDialog());

        //Изменение даты
        calendar.setOnClickListener(view -> calendar());
    }

    //Получить информацию из активности
    private void getData() {
        Intent i = getIntent();
        author = i.getStringExtra("author");
        title = i.getStringExtra("title");
        desk = i.getStringExtra("desk");
        urif = i.getStringExtra("uri");
        id = i.getStringExtra("id");
        date = i.getStringExtra("date");
        uploadId = i.getStringExtra("uploadId");
        arch = i.getBooleanExtra("archived", false);
    }


    //Проверка правильности ввода даты
    private void textWatcher(){
        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String mmyyyy = "MMГГГГ";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");
                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 6){
                        clean = clean + mmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int mon  = Integer.parseInt(clean.substring(0,2));
                        int year = Integer.parseInt(clean.substring(2,6));

                        if(mon < 1) mon = 1;
                        if(mon > 12) mon = 12;
                        if(year < 1900) year = 1900;
                        if(year > Integer.parseInt(y)) year = Integer.parseInt(y);
                        cal.set(Calendar.MONTH, mon-1);
                        cal.set(Calendar.YEAR, year);
                        clean = String.format("%02d%02d", mon, year);
                    }

                    clean = String.format("%s/%s", clean.substring(0, 2), clean.substring(2, 6));

                    sel = Math.max(sel, 0);
                    current = clean;
                    dateText.setText(current);
                    dateText.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        dateText.addTextChangedListener(tw);
    }

    //Подтверждение переноса объявления в архив
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

    //Сохранение изменений
    private void save() {
        if(checkDate()){
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
            Map<String, Object> hasMap = new HashMap<>();
            hasMap.put("author", authorText.getText().toString());
            hasMap.put("title", titleText.getText().toString());
            hasMap.put("desk", deskText.getText().toString());
            hasMap.put("uri", urif);
            hasMap.put("date", dateText.getText().toString());
            mDatabaseRef.child(uploadId).updateChildren(hasMap);
        }
    }

    //Сохраняем новое фото
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

    //Выбор откуда брать новую фотографию
    private void showChoicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet2layout);

        Button btGall = dialog.findViewById(R.id.gallery);
        Button btCam = dialog.findViewById(R.id.cam);

        //Из галереии
        btGall.setOnClickListener(view -> {
            ImagePicker.with(this).galleryOnly().crop().start();
            dialog.cancel();
        });

        //С камеры
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

    //Подтверждение удаления
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
                public void onCancelled(DatabaseError e) {

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

    //Выбор даты через прокрутку календаря
    private void calendar(){
        DatePickerDialog monthDatePickerDialog = new DatePickerDialog(EditActivity.this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(month+1 <= 9) dateText.setText("0" + Integer.toString(month+1) + Integer.toString(year));
                else dateText.setText(Integer.toString(month+1) + Integer.toString(year));
            }
        }, Integer.parseInt(y), Integer.parseInt(m) - 1, 1){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
            }
        };

        monthDatePickerDialog.setTitle("Выберите дату");
        monthDatePickerDialog.show();
    }

    //Проверка даты
    private boolean checkDate(){
        try {
            String date = dateText.getText().toString();
            String testMonth = date.substring(0,2);
            String testYear = date.substring(3,7);
            int temp = Integer.parseInt(testMonth);
            temp = Integer.parseInt(testYear);
            return true;
        }catch (Exception e){
            SuperiorToast.makeSuperiorToast(getApplicationContext(),
                            "Введите корректную дату")
                    .setToastIcon(getResources().getDrawable(R.drawable.warning))
                    .setColorToLeftVerticleStrip("#219BCC")
                    .showWithSimpleAnimation((ViewGroup) getWindow().getDecorView().getRootView() , SuperiorToast.ANIMATION_SLIDE_BOTTOM_ENTRY_EXIT);
            return false;
        }
    }
}
