package max51.com.vk.bookcrossing.ui.chats;

import static max51.com.vk.bookcrossing.util.encription.ECC.decrypt;
import static max51.com.vk.bookcrossing.util.encription.ECC.encrypt;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PrivateKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.chats.Messages;
import max51.com.vk.bookcrossing.util.chats.MessagesAdapter;
import max51.com.vk.bookcrossing.util.encription.ECC;

public class ChatActivity extends AppCompatActivity {  //Чат с пользователем

    private String reciverId;                         //id собеседника
    private String name;                              //Имя
    private String senderId;                          //id текущего пользователя
    private FirebaseDatabase database;                //бд
    private FirebaseAuth auth;                        //firebase авторизация

    private ImageView sendBtn;                        //Кнопка отправить
    private ImageView back;                           //Кнопка назад
    private EditText editMessage;                     //Поле ввода сообщения
    private TextView txtName;                         //Имя собеседника

    private RecyclerView messageAdapter;              //Пролистывающийся список
    private ArrayList<Messages> messagesArrayList;    //Массив сообщений

    private String senderRoom;                        //id команты
    private String reciverRoom;                       //id команты

    private MessagesAdapter adapter;                  //Адаптер

    private SharedPreferences sPref;    //Сохранение данных
    private String publicKey;
    private String privateKey;
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sPref = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        publicKey = sPref.getString("publicKey", "");
        privateKey = sPref.getString("privateKey", "");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.editMessage);
        messageAdapter = findViewById(R.id.messageAdapter);
        back = findViewById(R.id.back);
        txtName = findViewById(R.id.nameChat);

        name = getIntent().getStringExtra("name");
        reciverId = getIntent().getStringExtra("uid");
        key = getIntent().getStringExtra("key");

        txtName.setText(name);

        messagesArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setAdapter(adapter);

        senderId = auth.getUid();

        senderRoom = senderId + reciverId;
        reciverRoom = reciverId + senderId;


        //Поиск сообщений
        DatabaseReference ChatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        ChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    try {
                        messagesArrayList.add(new Messages(new String(decrypt(messages.getMessage(), string2PrivateKey(privateKey))), messages.getSenderId(), messages.getTimeStamp()));
                        messageAdapter.scrollToPosition(messagesArrayList.size() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Отправить сообщение
        sendBtn.setOnClickListener(view -> {
            String message = editMessage.getText().toString();
            if(message.isEmpty()){
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                return;
            }

            editMessage.setText("");
            Date date = new Date();

            Messages messagesSender = new Messages(getSenderText(message), senderId, date.getTime());
            Messages messagesReciver = new Messages(getReciverText(message), senderId, date.getTime());

            database = FirebaseDatabase.getInstance();
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(messagesSender)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats")
                                    .child(reciverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messagesReciver)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Map<String, Object> hasMap = new HashMap<>();
                                            hasMap.put("LastMessage", messagesSender);
                                            database.getReference().child("chats").child(senderRoom).updateChildren(hasMap);
                                            hasMap.clear();
                                            hasMap.put("LastMessage", messagesReciver);
                                            database.getReference().child("chats").child(reciverRoom).updateChildren(hasMap);
//                                            database.getReference().child("chats").child(senderRoom).child("LastMessage").setValue(messagesSender);
//                                            database.getReference().child("chats").child(reciverRoom).child("LastMessage").setValue(messagesReciver);
                                        }
                                    });
                        }
                    });
        });

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());
    }

    private String getSenderText(String message){
        try {
            byte[] cipherTxt = encrypt(message.getBytes(), ECC.string2PublicKey(publicKey));
            return Base64.getEncoder().encodeToString(cipherTxt);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private String getReciverText(String message){
        try {
            byte[] cipherTxt = encrypt(message.getBytes(), ECC.string2PublicKey(key));
            return Base64.getEncoder().encodeToString(cipherTxt);
        }catch (Exception e){
            return e.getMessage();
        }
    }

}