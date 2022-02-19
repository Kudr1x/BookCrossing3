package max51.com.vk.bookcrossing.ui.f2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.databinding.Fragment2Binding;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class Fragment2 extends Fragment {

    private HomeViewModel mViewModel;
    private ViewPagerAdapter adapter;
    RecyclerView viewPager;
    List<Bitmap> bitmapList = new ArrayList<Bitmap>();

    public static Fragment2 newInstance() {
        return new Fragment2();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        // adapter = new ViewPagerAdapter(ImageData.bitmapList);
        // viewPager.setAdapter(adapter);
        // viewPager.setPageTransformer(new CubeInScalingAnimation());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap movie1 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red);
        Bitmap movie2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red);
//        Bitmap movie2 = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.movie2);

        bitmapList.add(movie1);
        bitmapList.add(movie2);
//        bitmapList.add(movie2);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(bitmapList);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layoutManager);
        viewPager.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(viewPager);


    }

}