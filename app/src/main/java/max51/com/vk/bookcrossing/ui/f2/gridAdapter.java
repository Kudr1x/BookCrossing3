package max51.com.vk.bookcrossing.ui.f2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import max51.com.vk.bookcrossing.R;

public class gridAdapter extends BaseAdapter {
    private final int image[];
    private final String[] text1;
    private final String[] text2;
    Context context;

    public gridAdapter(int[] image, String[] text1, String[] text2, Context context) {
        this.image = image;
        this.text1 = text1;
        this.text2 = text2;
        this.context = context;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.vertical_item, null);

        ImageView img = view.findViewById(R.id.image);
        TextView tv1 = view.findViewById(R.id.text1);
        TextView tv2 = view.findViewById(R.id.text2);

        img.setImageResource(image[position]);
        tv1.setText(text1[position]);
        tv2.setText(text2[position]);

        return view;
    }
}
