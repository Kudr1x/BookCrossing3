package max51.com.vk.bookcrossing.ui.f2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import java.util.ArrayList;
import java.util.List;

import max51.com.vk.bookcrossing.MainActivity;
import max51.com.vk.bookcrossing.R;

public class Fragment2 extends Fragment {
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private final ArrayList<gridElement> gridElements = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gridElements.add(new gridElement( "Test1","Test2", R.color.purple_200));
        gridElements.add(new gridElement( "Test3","Test4", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));

        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red));
        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView grid = view.findViewById(R.id.grid_view);
        VerticalAdapter gridAdapter = new VerticalAdapter(gridElements, getContext());
        grid.setAdapter(gridAdapter);


        RecyclerView viewPager = view.findViewById(R.id.viewpager);
        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(bitmapList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layoutManager);
        viewPager.setAdapter(horizontalAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(viewPager);
    }
}