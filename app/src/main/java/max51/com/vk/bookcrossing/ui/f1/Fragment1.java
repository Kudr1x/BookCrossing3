package max51.com.vk.bookcrossing.ui.f1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f2.Elements;

public class Fragment1 extends Fragment {

    private final ArrayList<Elements> elementsArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        elementsArrayList.add(new Elements( "Test1","Test2", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test3","Test4", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));
        elementsArrayList.add(new Elements( "Test5","Test6", R.color.purple_200));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton btLoad = view.findViewById(R.id.floatingActionButton);

//todo
//        Animation anHide = AnimationUtils.loadAnimation(getContext(), R.anim.fab_hide);
//        Animation anShow = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show);

        RecyclerView rec1 = view.findViewById(R.id.userRecycleView);
        recAdapter recadapter = new recAdapter(elementsArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rec1.setLayoutManager(layoutManager);
        rec1.setAdapter(recadapter);

        rec1.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && btLoad.isShown())
                    btLoad.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    btLoad.setVisibility(View.VISIBLE);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        btLoad.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_loadActivity);
        });
    }
}