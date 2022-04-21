package max51.com.vk.bookcrossing.ui.f2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import max51.com.vk.bookcrossing.Elements;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.SelectListenerElement;

public class VerticalAdapter extends BaseAdapter {
    List<Elements>  element;
    Context context;
    private SelectListenerElement listener;

    public VerticalAdapter(List<Elements> element, Context context, SelectListenerElement listener) {
        this.element = element;
        this.context = context;
        this.listener = listener;
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
        TextView title = view.findViewById(R.id.title);
        TextView author = view.findViewById(R.id.author);
        LinearLayout main = view.findViewById(R.id.main_linear);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(element.get(position));
            }
        });


        title.setText(element.get(position).getTitle());
        author.setText(element.get(position).getAuthor());
        Picasso.get().load(element.get(position).getUri()).fit().centerCrop().into(img);

        return view;
    }
}
