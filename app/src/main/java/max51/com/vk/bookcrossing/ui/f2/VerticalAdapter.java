package max51.com.vk.bookcrossing.ui.f2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import max51.com.vk.bookcrossing.R;

public class VerticalAdapter extends BaseAdapter {
    List<gridElement>  element;
    Context context;

    public VerticalAdapter(List<gridElement> element, Context context) {
        this.element = element;
        this.context = context;
    }

    @Override
    public int getCount() {
        return element.size();
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

        img.setImageResource(element.get(position).image);
        tv1.setText(element.get(position).s1);
        tv2.setText(element.get(position).s2);

        return view;
    }
}
