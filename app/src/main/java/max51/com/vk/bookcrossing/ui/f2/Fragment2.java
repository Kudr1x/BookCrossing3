package max51.com.vk.bookcrossing.ui.f2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import max51.com.vk.bookcrossing.Elements;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.SelectListenerElement;

public class Fragment2 extends Fragment implements SelectListenerElement {
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private final ArrayList<Elements> gridElements = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private VerticalAdapter gridAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.rec));
        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.rec2));

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        ExpandableHeightGridView grid = view.findViewById(R.id.grid_view);
        grid.setAdapter(gridAdapter);
        grid.setExpanded(true);

        RecyclerView viewPager = view.findViewById(R.id.viewpager);
        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(bitmapList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layoutManager);
        viewPager.setAdapter(horizontalAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(viewPager);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                gridElements.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(!element.id.equals(FirebaseAuth.getInstance().getUid())){
                        gridElements.add(element);
                    }

                    createAdapter();

                    grid.setAdapter(gridAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAdapter(){
        gridAdapter = new VerticalAdapter(gridElements, getContext(), this);
    }

    @Override
    public void onItemClicked(Elements elements) {
        Bundle bundle = new Bundle();

        bundle.putString("title", elements.getTitle());
        bundle.putString("author", elements.getAuthor());
        bundle.putString("desk", elements.getDesk());
        bundle.putString("uri", elements.getUri());
        bundle.putString("id", elements.getId());

        Navigation.findNavController(getView()).navigate(R.id.action_navigation_dashboard_to_viewFragment, bundle);
    }
}