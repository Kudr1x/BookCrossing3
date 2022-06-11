package max51.com.vk.bookcrossing.util.chats;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.User;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<ChatUserItem> list;

    private SelectListenerChat listener;

    private String name;
    private String status;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView main;
        public CircleImageView circleImageView;
        public TextView title;
        public TextView lastMessage;

        public CircleImageView online;
        public CircleImageView offline;

        public ViewHolder(View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            circleImageView = itemView.findViewById(R.id.chatImage);
            title = itemView.findViewById(R.id.titleChat);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            online = itemView.findViewById(R.id.statusOnline);
            offline = itemView.findViewById(R.id.statusOffline);
        }
    }

    public ChatsAdapter(ArrayList<ChatUserItem> list, SelectListenerChat listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        ViewHolder evh = new ViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatUserItem item = list.get(position);

        holder.title.setText(item.getName());
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
        StorageReference fileReference = mStorageRef.child(list.get(position).getId() + "." + "jpeg");
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String urlProf = uri.toString();
                Picasso.get().load(urlProf).fit().centerCrop().into(holder.circleImageView);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(list.get(position).getId())){
                        name = user.getName();
                        holder.title.setText(name);

                        status = user.getStatus();

                        System.out.println(status);
                        if(Objects.equals(status, "online")){
                            holder.online.setVisibility(View.VISIBLE);
                        }else{
                            holder.offline.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.lastMessage.setText(item.getLastMsg());

        holder.main.setOnClickListener(view -> {
            listener.onItemClicked(new ChatUserItem(list.get(position).id, holder.title.getText().toString()));
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
//    public void filteredList(List<Elements> filteredList) {
//        mExampleList = filteredList;
//        notifyDataSetChanged();
//    }
}
