package max51.com.vk.bookcrossing.ui.f2;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
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
import java.util.Locale;

import max51.com.vk.bookcrossing.util.Elements;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.ExpandableHeightGridView;
import max51.com.vk.bookcrossing.util.HorizontalAdapter;
import max51.com.vk.bookcrossing.util.SelectListenerElement;
import max51.com.vk.bookcrossing.util.User;
import max51.com.vk.bookcrossing.util.VerticalAdapter;

public class Fragment2 extends Fragment implements SelectListenerElement{

    private final List<Bitmap> bitmapList = new ArrayList<>();
    private final ArrayList<Elements> gridElements = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private VerticalAdapter gridAdapter;
    private SearchView searchView;
    private String fav;

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

        searchView = view.findViewById(R.id.sv);

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
                    if(!element.id.equals(FirebaseAuth.getInstance().getUid()) && !element.getArchive()){
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        fav = user.getFavorite();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filter(String newText) {
        List<Elements> filteredList = new ArrayList<>();

        for(Elements i: gridElements){
            if(i.getTitle().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
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
        i.putExtra("title", elements.getTitle());
        i.putExtra("author", elements.getAuthor());
        i.putExtra("desk", elements.getDesk());
        i.putExtra("uri", elements.getUri());
        i.putExtra("id", elements.getId());
        i.putExtra("profileName", elements.getProfileName());
        i.putExtra("uploadId", elements.getUploadId());
        getActivity().startActivity(i);
    }
}