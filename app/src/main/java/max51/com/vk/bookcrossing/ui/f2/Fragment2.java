package max51.com.vk.bookcrossing.ui.f2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import max51.com.vk.bookcrossing.util.custom.TabView;
import max51.com.vk.bookcrossing.util.elements.Elements;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.custom.ExpandableHeightGridView;
import max51.com.vk.bookcrossing.util.HorizontalAdapter;
import max51.com.vk.bookcrossing.util.elements.SelectListenerElement;
import max51.com.vk.bookcrossing.util.User;
import max51.com.vk.bookcrossing.util.elements.VerticalAdapter;

public class Fragment2 extends Fragment implements SelectListenerElement{

    private final List<Bitmap> bitmapList = new ArrayList<>();
    private final ArrayList<Elements> gridElements = new ArrayList<>();
    private VerticalAdapter gridAdapter;
    private String fav;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabView tabView;
    private ExpandableHeightGridView grid;
    private String city;
    private String region;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.rec));
        bitmapList.add(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.rec2));

        tabView = view.findViewById(R.id.tabv_tab);
        SearchView searchView = view.findViewById(R.id.sv);
        swipeRefreshLayout = view.findViewById(R.id.swipe);

        grid = view.findViewById(R.id.grid_view);
        grid.setAdapter(gridAdapter);
        grid.setExpanded(true);

        RecyclerView viewPager = view.findViewById(R.id.viewpager);
        HorizontalAdapter horizontalAdapter = new HorizontalAdapter(bitmapList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        viewPager.setLayoutManager(layoutManager);
        viewPager.setAdapter(horizontalAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(viewPager);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gridAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        getUserData();

        selectData(0);

        selectFilter();
    }

    private void selectData(int filter) {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                gridElements.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(!element.id.equals(FirebaseAuth.getInstance().getUid()) && !element.getArchived()){
                        switch (filter){
                            case 0:
                                if(Objects.equals(element.getCity(), city)) gridElements.add(element);
                                break;
                            case 1:
                                if(Objects.equals(element.getRegion(), region)) gridElements.add(element);
                                break;
                            case 2:
                                gridElements.add(element);
                                break;
                        }
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

    private void getUserData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        fav = user.getFavorite();
                        city = user.getCity();
                        region = user.getRegion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void selectFilter() {
        tabView.setOnTabSelectedListener(new TabView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                selectData(index);
            }
        });
    }

    private void filter(String newText) {
        List<Elements> filteredList = new ArrayList<>();

        for(Elements i: gridElements){
            String mainTitle = i.getTitle() + " " + i.getDate();
            if(mainTitle.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filteredList.add(i);
            }
        }

        try{ gridAdapter.filteredList(filteredList);
        }catch (Exception ignored){ }

    }

    public void createAdapter(){
        gridAdapter = new VerticalAdapter(gridElements, getContext(), this, getActivity());
    }

    @Override
    public void onItemClicked(Elements elements) {
        sendData(elements);
    }

    private void sendData(Elements elements) {
        Intent i = new Intent(getActivity().getBaseContext(), ViewActivity.class);

        i.putExtra("fav", fav);
        i.putExtra("city", elements.getCity());
        i.putExtra("region", elements.getRegion());
        i.putExtra("title", elements.getTitle());
        i.putExtra("author", elements.getAuthor());
        i.putExtra("desk", elements.getDesk());
        i.putExtra("uri", elements.getUri());
        i.putExtra("id", elements.getId());
        i.putExtra("profileName", elements.getProfileName());
        i.putExtra("uploadId", elements.getUploadId());
        i.putExtra("date", elements.getDate());
        getActivity().startActivity(i);
    }
}