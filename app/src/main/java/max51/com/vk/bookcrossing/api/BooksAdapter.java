package max51.com.vk.bookcrossing.api;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import max51.com.vk.bookcrossing.R;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.CustomViewHolder> {
    private List<Item> volumeInfo;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        VolumeInfo volumeInfo = this.volumeInfo.get(position).getVolumeInfo();
        holder.bookTitle.setText(volumeInfo.getTitle());
        if (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) {
            holder.bookAuthor.setText(volumeInfo.getAuthors().get(0));
        }
    }

    @Override
    public int getItemCount() {
        return volumeInfo == null ? 0 : volumeInfo.size();
    }

    public void setVolumeInfo(final List<Item> volumeInfo) {
        this.volumeInfo = volumeInfo;
        notifyDataSetChanged();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor;

        CustomViewHolder(View view) {
            super(view);
            bookTitle = view.findViewById(R.id.bookTitle);
            bookAuthor = view.findViewById(R.id.bookAuthor);
        }
    }
}
