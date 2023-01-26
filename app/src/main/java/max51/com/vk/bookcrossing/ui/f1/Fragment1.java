package max51.com.vk.bookcrossing.ui.f1;

import static android.content.Intent.getIntent;

import static max51.com.vk.bookcrossing.util.encription.ECC.sign;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PrivateKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.string2PublicKey;
import static max51.com.vk.bookcrossing.util.encription.ECC.verify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.ui.f3.Fragment3;
import max51.com.vk.bookcrossing.util.User;
import max51.com.vk.bookcrossing.util.elements.Elements;
import max51.com.vk.bookcrossing.util.elements.RecAdapter;
import max51.com.vk.bookcrossing.util.elements.SelectListenerElement;
import max51.com.vk.bookcrossing.util.encription.ECC;

public class Fragment1 extends Fragment implements SelectListenerElement {  //Просмотр собственных объявлений

    private final ArrayList<Elements> elementsArrayList = new ArrayList<>();  //Массив объявлений
    private DatabaseReference mDatabaseRef;                                   //База данных realtime
    private RecyclerView recyclerView;                                        //Прокручиваемый список
    private RecAdapter recAdapter;                                            //Адаптер
    private SharedPreferences sPref;    //Сохранение данных


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton btLoad = view.findViewById(R.id.floatingActionButton);
        TextView textView = view.findViewById(R.id.startMessage);
        SearchView searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.userRecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        sPref = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        getKey();

        clearKey();

        //Находим все наши объявления
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                elementsArrayList.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Elements element = postSnapshot.getValue(Elements.class);
                    if(element.id.equals(FirebaseAuth.getInstance().getUid()) && !element.getArchived()){
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Скрываем кнопочку при прокрутке
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

        //Переход в создание объявления
        btLoad.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_loadActivity));

        //Поиск объвлений
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
    }

    //Фильтрация объявлений по названию
    private void filter(String newText) {
        List<Elements> filteredList = new ArrayList<>();

        for(Elements i: elementsArrayList){
            if(i.getTitle().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filteredList.add(i);
            }
        }

        if(!filteredList.isEmpty()){
            recAdapter.filteredList(filteredList);
        }
    }

    //Переход в активность редактирования
    @Override
    public void onItemClicked(Elements elements) {
        Intent i = new Intent(getActivity().getBaseContext(), EditActivity.class);

        i.putExtra("title", elements.getTitle());
        i.putExtra("author", elements.getAuthor());
        i.putExtra("desk", elements.getDesk());
        i.putExtra("uri", elements.getUri());
        i.putExtra("id", elements.getId());
        i.putExtra("uploadId", elements.getUploadId());
        i.putExtra("date", elements.getDate());
        getActivity().startActivity(i);
    }

    //Создание адаптера
    public void createAdapter(){
        recAdapter = new RecAdapter(elementsArrayList, this, getActivity());
    }

    private void getKey() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString("publicKey", user.getPublicKey());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private boolean checkKey(){
        if(!sPref.getString("privateKey", "").equals("")){
            try {
                byte[] arr = sign("testKey", string2PrivateKey(sPref.getString("privateKey", "")));
                return verify("testKey", arr, string2PublicKey(sPref.getString("publicKey", "")));
            }catch (Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    private void clearKey(){
        if(!checkKey()){
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString("privateKey", "");
            editor.apply();
        }
    }
}