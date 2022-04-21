package max51.com.vk.bookcrossing.ui.f1;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.Elements;
import max51.com.vk.bookcrossing.SelectListenerElement;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ExampleViewHolder> {
    private ArrayList<Elements> mExampleList;
    private SelectListenerElement listener;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public LinearLayout linearLayout;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.main);
            mImageView = itemView.findViewById(R.id.imageV);
            mTextView1 = itemView.findViewById(R.id.name);
            mTextView2 = itemView.findViewById(R.id.name2);
        }
    }

    public RecAdapter(ArrayList<Elements> exampleList, SelectListenerElement listener) {
        mExampleList = exampleList;
        this.listener = listener;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_vertical_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Elements currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem.getTitle());
        holder.mTextView2.setText(currentItem.getAuthor());
        Picasso.get().load(currentItem.getUri()).fit().centerCrop().into(holder.mImageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(mExampleList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
