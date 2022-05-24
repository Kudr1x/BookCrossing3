package max51.com.vk.bookcrossing.ui.f3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f1.EditActivity;
import max51.com.vk.bookcrossing.ui.f2.ViewActivity;
import max51.com.vk.bookcrossing.util.Elements;
import max51.com.vk.bookcrossing.util.RecAdapter;
import max51.com.vk.bookcrossing.util.SelectListenerElement;


public class ArchiveActivity extends AppCompatActivity implements SelectListenerElement {

    private final ArrayList<Elements> elementsArrayList = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private RecyclerView recyclerView;
    private RecAdapter recAdapter;
    private TextView textView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        TextView textView = findViewById(R.id.startMessage);
        SearchView searchView = findViewById(R.id.searchArch);
        ImageView back = findViewById(R.id.backArch);

        recyclerView = findViewById(R.id.archRecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ArchiveActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                elementsArrayList.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(Objects.equals(element.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid()) && element.getArchived()){
                        elementsArrayList.add(element);
                    }

                    createAdapter();

                    if(elementsArrayList.isEmpty()){
                        textView.setVisibility(View.VISIBLE);
                    }else {
                        textView.setVisibility(View.GONE);
                    }

                    recyclerView.setAdapter(recAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ArchiveActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        back.setOnClickListener(view -> onBackPressed());
    }

    private void filter(String newText) {
        List<Elements> filteredList = new ArrayList<>();

        for(Elements i: elementsArrayList){
            if(i.getTitle().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filteredList.add(i);
            }
        }

        recAdapter.filteredList(filteredList);
    }

    @Override
    public void onItemClicked(Elements elements) {
        Intent i = new Intent(ArchiveActivity.this, EditActivity.class);

        i.putExtra("title", elements.getTitle());
        i.putExtra("author", elements.getAuthor());
        i.putExtra("desk", elements.getDesk());
        i.putExtra("uri", elements.getUri());
        i.putExtra("id", elements.getId());
        i.putExtra("profileName", elements.getProfileName());
        i.putExtra("uploadId", elements.getUploadId());
        i.putExtra("archived", elements.getArchived());
        this.startActivity(i);
    }

    public void createAdapter(){
        recAdapter = new RecAdapter(elementsArrayList, this, this);
    }
}