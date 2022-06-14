package max51.com.vk.bookcrossing.api;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import max51.com.vk.bookcrossing.R;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.CustomViewHolder> {  //Адапетр для подсказок по книгам
    private List<Item> volumeInfo;
    private SelectListener listener;

    public BooksAdapter(final List<Item> volumeInfo, SelectListener listener){   //Конструктор адаптера
        this.volumeInfo = volumeInfo;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bookTitle.setText(volumeInfo.get(position).getVolumeInfo().getTitle());
        if (volumeInfo.get(position).getVolumeInfo().getAuthors() != null && !volumeInfo.get(position).getVolumeInfo().getAuthors().isEmpty()) {
            holder.bookAuthor.setText(volumeInfo.get(position).getVolumeInfo().getAuthors().get(0));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(volumeInfo.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return volumeInfo == null ? 0 : volumeInfo.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView bookTitle, bookAuthor;
        public LinearLayout linearLayout;

        CustomViewHolder(View view) {
            super(view);
            bookTitle = view.findViewById(R.id.bookTitle);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            linearLayout = view.findViewById(R.id.main_container);
        }
    }
}
