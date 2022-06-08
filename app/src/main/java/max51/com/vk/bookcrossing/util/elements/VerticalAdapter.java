package max51.com.vk.bookcrossing.util.elements;

import android.app.Activity;
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

import max51.com.vk.bookcrossing.R;

public class VerticalAdapter extends BaseAdapter{
    List<Elements>  element;
    Context context;
    Activity activity;

    private SelectListenerElement listener;

    public VerticalAdapter(List<Elements> element, Context context, SelectListenerElement listener, Activity activity) {
        this.element = element;
        this.context = context;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return element.size();
    }

    @Override
    public Object getItem(int position) {
        return element.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        thread thread = new thread(element, position, img, title, author, activity);
        thread.start();

        return view;
    }

    public void filteredList(List<Elements> filteredList){
        element = filteredList;
        notifyDataSetChanged();
    }
}

class thread extends Thread{

    List<Elements> element;
    int position;
    ImageView img;
    TextView title;
    TextView author;
    Activity activity;

    public thread(List<Elements> element, int position, ImageView img, TextView title, TextView author, Activity activity) {
        this.element = element;
        this.position = position;
        this.img = img;
        this.title = title;
        this.author = author;
        this.activity = activity;
    }

    @Override
    public void run(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(element.get(position).getUri()).fit().centerCrop().into(img);
                title.setText(element.get(position).getTitle() + "," + element.get(position).getDate());
                author.setText(element.get(position).getAuthor());
            }
        });
    }
}
