package max51.com.vk.bookcrossing.ui.f2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import java.util.ArrayList;
import java.util.List;

import max51.com.vk.bookcrossing.R;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class Fragment2 extends Fragment {
    private HorizontalAdapter horizontalAdapter;
    private VerticalAdapter gridAdapter;
    RecyclerView viewPager;
    List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    GridView grid;

    int im[]= {R.color.purple_200, R.color.purple_200, R.color.purple_200, R.color.purple_200, R.color.purple_200};

    String str1[] = {"Test1", "Test2", "Test3", "test7", "Продам гараж"};

    String str2[] = {"Test4", "Test5", "Test6", "test8", "120000 руб"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bitmap movie1 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red);
        Bitmap movie2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red);

        bitmapList.add(movie1);
        bitmapList.add(movie2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        grid = view.findViewById(R.id.grid_view);
        gridAdapter = new VerticalAdapter(im,str1, str2,getContext());
        grid.setAdapter(gridAdapter);


        horizontalAdapter = new HorizontalAdapter(bitmapList);
        viewPager = view.findViewById(R.id.viewpager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layoutManager);
        viewPager.setAdapter(horizontalAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(viewPager);

        Context context = getContext();

        ScrollingPagerIndicator spi = new ScrollingPagerIndicator(context);

        spi.attachToRecyclerView(viewPager);
    }
}