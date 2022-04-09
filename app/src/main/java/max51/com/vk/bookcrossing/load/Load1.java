package max51.com.vk.bookcrossing.load;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.api.ApiService;
import max51.com.vk.bookcrossing.api.BookResponse;
import max51.com.vk.bookcrossing.api.BooksAdapter;
import max51.com.vk.bookcrossing.api.Item;
import max51.com.vk.bookcrossing.api.RetroClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Load1 extends Fragment {

    private static final int MAX_RESULTS = 5;

    private List<Item> volumeInfoList;
    private ApiService api;
    private BooksAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load1, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setText(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setText(editable.toString());
            }

            void setText(String s){
                try{
                    adapter = new BooksAdapter();
                    recyclerView.setAdapter(adapter);
                }catch (Exception ignored){ }
                doSearch(s);
            }
        });


    }

    private void doSearch(final String query) {
        Call<BookResponse> call = api.getMyJSON(query, MAX_RESULTS);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful()) {
                    volumeInfoList = response.body().getItems();
                    recyclerView.smoothScrollToPosition(0);
                    adapter.setVolumeInfo(volumeInfoList);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) { }
        });
    }
}