package max51.com.vk.bookcrossing.ui.f2;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import max51.com.vk.bookcrossing.R;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ImageHelper> {

   List<Bitmap> bitmapList;

    public HorizontalAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public ImageHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rows_movie,parent,false);
        return new ImageHelper(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHelper holder, int position) {
            Bitmap bitmap = bitmapList.get(position);
         holder.moviewImage.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    class  ImageHelper extends RecyclerView.ViewHolder{
        ImageView moviewImage;
        public ImageHelper(@NonNull View itemView) {
            super(itemView);
            moviewImage = itemView.findViewById(R.id.movieImage);
        }
    }
}
