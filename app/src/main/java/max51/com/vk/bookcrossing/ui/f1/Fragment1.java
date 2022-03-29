package max51.com.vk.bookcrossing.ui.f1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f2.VerticalAdapter;
import max51.com.vk.bookcrossing.ui.f2.gridElement;

public class Fragment1 extends Fragment {

    private final ArrayList<gridElement> gridElements = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        gridElements.add(new gridElement( "Test1","Test2", R.color.purple_200));
        gridElements.add(new gridElement( "Test3","Test4", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));
        gridElements.add(new gridElement( "Test5","Test6", R.color.purple_200));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btLoad = view.findViewById(R.id.buttonLoad);

        GridView grid = view.findViewById(R.id.userGrid);
        max51.com.vk.bookcrossing.ui.f2.VerticalAdapter gridAdapter = new VerticalAdapter(gridElements, getContext());
        grid.setAdapter(gridAdapter);

    }
}