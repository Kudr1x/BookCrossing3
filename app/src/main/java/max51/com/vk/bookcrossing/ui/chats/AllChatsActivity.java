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

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.chats.ChatUserItem;
import max51.com.vk.bookcrossing.util.chats.ChatsAdapter;
import max51.com.vk.bookcrossing.util.chats.SelectListenerChat;

public class AllChatsActivity extends AppCompatActivity implements SelectListenerChat {

    String yourId;
    String otherId;

    FirebaseDatabase database;
    RecyclerView recyclerView;
    ImageView back;
    ArrayList<ChatUserItem> chats = new ArrayList<>();
    ArrayList<String> chats1 = new ArrayList<>();

    SelectListenerChat selectListenerChat;

    ChatsAdapter chatsAdapter;

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
                        chats.add(new ChatUserItem(otherId));
                        chatsAdapter = new ChatsAdapter(chats, selectListenerChat);
                        recyclerView.setAdapter(chatsAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onItemClicked(ChatUserItem item) {
        Intent i = new Intent(AllChatsActivity.this, ChatActivity.class);
        i.putExtra("uid", item.getId());
        i.putExtra("name", item.getName());
        startActivity(i);
    }
}