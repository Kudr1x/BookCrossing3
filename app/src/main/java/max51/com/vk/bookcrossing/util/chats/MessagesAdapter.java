package max51.com.vk.bookcrossing.util.chats;

import static max51.com.vk.bookcrossing.util.encription.ECC.decrypt;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PrivateKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.chats.Messages;

public class MessagesAdapter extends RecyclerView.Adapter {   //Адаптер сообщенинй в диалоге

    Context context;                               //Контекст
    ArrayList<Messages> messagesArrayList;         //Массив сообщенинй
    final static int ITEM_SENDER = 1;              //Константа для обозначения типа сообщения
    final static int ITEM_RECIVR = 2;              //Константа для обозначения типа сообщения

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {  //Конструктор
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENDER){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item, parent, false);   //Сообщение текущего пользователя
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item, parent, false);   //Сообщение собеседника
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);

        try {
            if(holder.getClass() == SenderViewHolder.class){
                SenderViewHolder viewHolder = (SenderViewHolder) holder;
//            try {
//                viewHolder.textView.setText(new String(decrypt(messages.getMessage(), string2PrivateKey(privateKey))));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
                viewHolder.textView.setText(messages.getMessage());
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
                StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + "jpeg");
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlProf = uri.toString();
                        Picasso.get().load(urlProf).fit().centerCrop().into(viewHolder.imageView);
                    }
                });
            }else {
                ReciverViewHolder viewHolder = (ReciverViewHolder) holder;
                viewHolder.textView.setText(messages.getMessage());
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars/");
                StorageReference fileReference = mStorageRef.child(messages.getSenderId() + "." + "jpeg");
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlProf = uri.toString();
                        Picasso.get().load(urlProf).fit().centerCrop().into(viewHolder.imageView);
                    }
                });
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SENDER;
        }else {
            return ITEM_RECIVR;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profileImage);
            textView = itemView.findViewById(R.id.txtMessages);
        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profileImage);
            textView = itemView.findViewById(R.id.txtMessages);
        }
    }
}
