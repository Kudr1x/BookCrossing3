package max51.com.vk.bookcrossing.ui.f3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.shubh.superiortoastlibrary.SuperiorToast;
import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f2.ViewActivity;
import max51.com.vk.bookcrossing.util.elements.Elements;
import max51.com.vk.bookcrossing.util.elements.RecAdapter;
import max51.com.vk.bookcrossing.util.elements.SelectListenerElement;

public class FavoriteActivity extends AppCompatActivity implements SelectListenerElement {   //Просмотр избранных объявлений

    private final ArrayList<Elements> elementsArrayList = new ArrayList<>();       //Массив объявлений
    private DatabaseReference mDatabaseRef;                                        //бд
    private RecyclerView recyclerView;                                             //Прокручиваемый список
    private RecAdapter recAdapter;                                                 //Адаптер
    private String fav;                                                            //id избраннных объявлений

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        TextView textView = findViewById(R.id.startMessage);
        SearchView searchView = findViewById(R.id.searchFavorite);
        ImageView back = findViewById(R.id.backFav);

        recyclerView = findViewById(R.id.favoriteRecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        Intent i = getIntent();
        fav = i.getStringExtra("fav");
        String[] separated = fav.split("\\|");

        //Берём избранные объявления из бд
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                elementsArrayList.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(Arrays.asList(separated).contains(element.getUploadId()) && !element.getArchived()){
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
                SuperiorToast.makeSuperiorToast(getApplicationContext(),
                                error.getMessage())
                        .setToastIcon(getResources().getDrawable(R.drawable.warning))
                        .setColorToLeftVerticleStrip("#219BCC")
                        .showWithSimpleAnimation((ViewGroup) getWindow().getDecorView().getRootView() , SuperiorToast.ANIMATION_SLIDE_BOTTOM_ENTRY_EXIT);

            }
        });

        //Поиск по названию
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

        //Кнопочка назад
        back.setOnClickListener(view -> onBackPressed());
    }

    //Поиск по названию
    private void filter(String newText) {
        List<Elements> filteredList = new ArrayList<>();

        for(Elements i: elementsArrayList){
            if(i.getTitle().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filteredList.add(i);
            }
        }

        recAdapter.filteredList(filteredList);
    }

    //переход в просмотр и редактирование
    @Override
    public void onItemClicked(Elements elements) {
        Intent i = new Intent(FavoriteActivity.this, ViewActivity.class);

        i.putExtra("title", elements.getTitle());
        i.putExtra("author", elements.getAuthor());
        i.putExtra("desk", elements.getDesk());
        i.putExtra("uri", elements.getUri());
        i.putExtra("id", elements.getId());
        i.putExtra("uploadId", elements.getUploadId());
        i.putExtra("fav", fav);
        this.startActivity(i);
    }

    //Создание адаптера
    public void createAdapter(){
        recAdapter = new RecAdapter(elementsArrayList, this, this);
    }
}