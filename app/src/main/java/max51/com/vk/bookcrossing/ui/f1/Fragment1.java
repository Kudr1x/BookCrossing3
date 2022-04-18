package max51.com.vk.bookcrossing.ui.f1;

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
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.Elements;

public class Fragment1 extends Fragment {

    private final ArrayList<Elements> elementsArrayList = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private RecAdapter recAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton btLoad = view.findViewById(R.id.floatingActionButton);


        recyclerView = view.findViewById(R.id.userRecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                elementsArrayList.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(element.id.equals(FirebaseAuth.getInstance().getUid())){
                        elementsArrayList.add(element);
                    }

                    recAdapter = new RecAdapter(elementsArrayList);

                    recyclerView.setAdapter(recAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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