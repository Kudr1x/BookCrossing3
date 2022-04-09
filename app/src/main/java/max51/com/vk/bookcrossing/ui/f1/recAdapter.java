package max51.com.vk.bookcrossing.ui.f1;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f2.Elements;

public class recAdapter extends RecyclerView.Adapter<recAdapter.ExampleViewHolder> {
    private ArrayList<Elements> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageV);
            mTextView1 = itemView.findViewById(R.id.name);
            mTextView2 = itemView.findViewById(R.id.name2);
        }
    }

    public recAdapter(ArrayList<Elements> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_vertical_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Elements currentItem = mExampleList.get(position);
        holder.mImageView.setImageResource(currentItem.getImage());
        holder.mTextView1.setText(currentItem.getS1());
        holder.mTextView2.setText(currentItem.getS2());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
