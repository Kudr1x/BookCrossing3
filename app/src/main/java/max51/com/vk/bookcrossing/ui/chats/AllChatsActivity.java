package max51.com.vk.bookcrossing.ui.chats;

import static max51.com.vk.bookcrossing.util.encription.ECC.decrypt;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PrivateKey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FilterOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f3.FavoriteActivity;
import max51.com.vk.bookcrossing.ui.login.Register;
import max51.com.vk.bookcrossing.util.User;
import max51.com.vk.bookcrossing.util.chats.ChatUserItem;
import max51.com.vk.bookcrossing.util.chats.ChatsAdapter;
import max51.com.vk.bookcrossing.util.chats.SelectListenerChat;

public class AllChatsActivity extends AppCompatActivity implements SelectListenerChat {  //Все чаты

    private String yourId;                                          //Свой id
    private String otherId;                                         //Чужой id
    private String lastMsg;                                         //Последнее сообщение
    private FirebaseDatabase database;                              //бд
    private RecyclerView recyclerView;                              //Пролистывающийся список
    private ImageView back;                                         //Кнопка назад
    private ArrayList<ChatUserItem> chats = new ArrayList<>();      //Массив чатов
    private ArrayList<String> chats1 = new ArrayList<>();           //Массив id пользователей
    private SelectListenerChat selectListenerChat;                  //Слушатель кликов
    private ChatsAdapter chatsAdapter;                              //Адаптер
    private String key;
    private String gName;
    private String gUid;
    private String privateKey;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);
        recyclerView = findViewById(R.id.allChats);
        back = findViewById(R.id.back);

        sPref = getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        privateKey = sPref.getString("privateKey", "");

        selectListenerChat = this;

        yourId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();

        //Поиск всех чатов текущего пользователя
        DatabaseReference ChatReference = database.getReference().child("chats");
        ChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    if(key.substring(0, 28).equals(yourId)){
                        otherId = key.substring(28);
                        chats1.add(otherId);
                        DatabaseReference lm = ChatReference.child(key).child("LastMessage").child("message");
                        lm.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    lastMsg = snapshot.getValue().toString();
                                    chats.add(new ChatUserItem(otherId, new String(decrypt(lastMsg, string2PrivateKey(privateKey))), true));
                                } catch (Exception e) {
                                    chats.add(new ChatUserItem(otherId, "msg", true));
                                }
                                chatsAdapter = new ChatsAdapter(chats, selectListenerChat);
                                recyclerView.setAdapter(chatsAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());
    }

    private void searchPublicKey() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent i = new Intent(AllChatsActivity.this, ChatActivity.class);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(otherId)){
                        key = user.getPublicKey();
                        i.putExtra("key", key);
                        i.putExtra("uid", gUid);
                        i.putExtra("name", gName);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //Открытие диалога
    @Override
    public void onItemClicked(ChatUserItem item) {
        gUid = item.getId();
        gName = item.getName();
        searchPublicKey();
    }
}