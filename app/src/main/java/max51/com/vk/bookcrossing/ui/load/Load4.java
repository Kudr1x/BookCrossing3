package max51.com.vk.bookcrossing.ui.load;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import max51.com.vk.bookcrossing.R;

public class Load4 extends Fragment {

    String author;
    String title;
    String desk;
    String id;
    String y;
    String m;

    ImageView calendar;
    EditText editDate;
    Button next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_load4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        author = getArguments().getString("author");
        title = getArguments().getString("title");
        desk = getArguments().getString("desk");
        calendar = view.findViewById(R.id.calendar);
        editDate = view.findViewById(R.id.editDate);
        next = view.findViewById(R.id.next4);

        String timeStamp = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());

        y = timeStamp.substring(0,4);
        m = timeStamp.substring(4,6);

        DatePickerDialog monthDatePickerDialog = new DatePickerDialog(getContext(),
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(month+1 <= 9) editDate.setText("0" + Integer.toString(month+1) + Integer.toString(year));
                else editDate.setText(Integer.toString(month+1) + Integer.toString(year));
            }
        }, Integer.parseInt(y), Integer.parseInt(m) - 1, 1){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
            }
        };

        next.setOnClickListener(view1 -> {
            try {
                String date = editDate.getText().toString();
                String testMonth = date.substring(0,2);
                String testYear = date.substring(3,7);
                int temp = Integer.parseInt(testMonth);
                temp = Integer.parseInt(testYear);

                Bundle bundle = new Bundle();
                bundle.putString("author", author);
                bundle.putString("title", title);
                bundle.putString("desk", desk);
                bundle.putString("date", date);
                Navigation.findNavController(view).navigate(R.id.action_load4_to_load5, bundle);
            }catch (Exception e){
                Snackbar.make(view, "Введите корректную дату", Snackbar.LENGTH_LONG).show();
            }
        });

        monthDatePickerDialog.setTitle("Выберите дату");

        calendar.setOnClickListener(view1 -> monthDatePickerDialog.show());

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String mmyyyy = "MMГГГГ";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");
                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 6){
                        clean = clean + mmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int mon  = Integer.parseInt(clean.substring(0,2));
                        int year = Integer.parseInt(clean.substring(2,6));

                        if(mon < 1) mon = 1;
                        if(mon > 12) mon = 12;
                        if(year < 1900) year = 1900;
                        if(year > Integer.parseInt(y)) year = Integer.parseInt(y);
                        cal.set(Calendar.MONTH, mon-1);
                        cal.set(Calendar.YEAR, year);
                        clean = String.format("%02d%02d", mon, year);
                    }

                    clean = String.format("%s/%s", clean.substring(0, 2), clean.substring(2, 6));

                    sel = Math.max(sel, 0);
                    current = clean;
                    editDate.setText(current);
                    editDate.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editDate.addTextChangedListener(tw);
    }
}