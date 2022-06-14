package max51.com.vk.bookcrossing.ui.chats;

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
import java.util.Date;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.chats.Messages;
import max51.com.vk.bookcrossing.util.chats.MessagesAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.editMessage);
        messageAdapter = findViewById(R.id.messageAdapter);
        back = findViewById(R.id.back);
        txtName = findViewById(R.id.nameChat);

        name = getIntent().getStringExtra("name");
        reciverId = getIntent().getStringExtra("uid");

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
                    messagesArrayList.add(messages);
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

            Messages messages = new Messages(message, senderId, date.getTime());

            database = FirebaseDatabase.getInstance();
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(messages)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats")
                                    .child(reciverRoom)
                                    .child("messages")
                                    .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            database.getReference().child("chats").child(senderRoom).child("LastMessage").setValue(message);
                                            database.getReference().child("chats").child(reciverRoom).child("LastMessage").setValue(message);
                                        }
                                    });
                        }
                    });
        });

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());
    }
}