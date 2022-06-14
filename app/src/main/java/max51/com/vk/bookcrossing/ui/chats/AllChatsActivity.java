package max51.com.vk.bookcrossing.ui.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import max51.com.vk.bookcrossing.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);
        recyclerView = findViewById(R.id.allChats);
        back = findViewById(R.id.back);

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
                        DatabaseReference lm = ChatReference.child(key).child("LastMessage");
                        lm.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                lastMsg = snapshot.getValue().toString();
                                chats.add(new ChatUserItem(otherId, lastMsg, true));
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

    //Открытие диалога
    @Override
    public void onItemClicked(ChatUserItem item) {
        Intent i = new Intent(AllChatsActivity.this, ChatActivity.class);
        i.putExtra("uid", item.getId());
        i.putExtra("name", item.getName());
        startActivity(i);
    }

    //меняет статус в сети пользователь или нет
    private void setStatus(String status){
        Map<String, Object> hasMap = new HashMap<>();
        hasMap.put("status", status);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hasMap);
    }

    //Смена статуса
    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }
}