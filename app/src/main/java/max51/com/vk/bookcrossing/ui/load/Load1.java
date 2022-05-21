package max51.com.vk.bookcrossing.ui.load;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.api.ApiService;
import max51.com.vk.bookcrossing.api.BookResponse;
import max51.com.vk.bookcrossing.api.BooksAdapter;
import max51.com.vk.bookcrossing.api.Item;
import max51.com.vk.bookcrossing.api.RetroClient;
import max51.com.vk.bookcrossing.api.SelectListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Load1 extends Fragment implements SelectListener {

    private static final int MAX_RESULTS = 5;

    private String title;
    private String author;
    private List<Item> volumeInfoList;
    private ApiService api;
    private BooksAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        api = RetroClient.getApiService();
        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        EditText editText = view.findViewById(R.id.autoCompleteTextEdit);
        Button bt = view.findViewById(R.id.next1);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { edit(editable); }
        });

        bt.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            title = editText.getText().toString();

            if(title.isEmpty()){
                editText.setError("Введите название");
                editText.requestFocus();
                return;
            }

            try{
                bundle.putString("author", author);
                bundle.putString("title", title);
            }catch(Exception ignored){ }
            Navigation.findNavController(view).navigate(R.id.action_load1_to_load2, bundle);
        });
    }

    private void doSearch(final String query) {
        Call<BookResponse> call = api.getMyJSON(query, MAX_RESULTS);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response){
                if (response.isSuccessful()) {
                    volumeInfoList = response.body().getItems();
                    recyclerView.smoothScrollToPosition(0);
                    setAdapters();
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) { }
        });
    }

    @Override
    public void onItemClicked(Item item) {
        EditText editText = getView().findViewById(R.id.autoCompleteTextEdit);
        editText.setText(item.getVolumeInfo().getTitle());
        try{
            title = item.getVolumeInfo().getTitle();
            if(item.getVolumeInfo().getAuthors().size() != 0) author = item.getVolumeInfo().getAuthors().get(0);
        }catch (Exception e){
            author = "";
        }
    }

    public void setAdapters(){
        adapter = new BooksAdapter(volumeInfoList, this);
        try{ recyclerView.setAdapter(adapter); }catch (Exception ignored){ }
    }

    public void edit(Editable editable){
        doSearch(editable.toString());
        author = "";
    }
}