package max51.com.vk.bookcrossing.util.city;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import max51.com.vk.bookcrossing.R;
import max51.com.vk.bookcrossing.util.city.City;

public class CityAdapter extends ArrayAdapter<City> {
    private List<City> cityList;

    public CityAdapter(@NonNull Context context, @NonNull List<City> list) {
        super(context, 0, list);
        cityList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return cityFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_hint, parent, false);
        }

        TextView city = convertView.findViewById(R.id.city);
        TextView region = convertView.findViewById(R.id.region);

        City countryItem = getItem(position);

        if(countryItem != null){
            city.setText(countryItem.getCity());
            region.setText(countryItem.getRegion());
        }

        return convertView;
    }

    private Filter cityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<City> suggestion = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                suggestion.addAll(cityList);
            }else {
                String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();

                for(City item : cityList){
                    if(item.getCity().toLowerCase(Locale.ROOT).contains(filterPattern)){
                        suggestion.add(item);
                    }
                }
            }

            results.values = suggestion;
            results.count = suggestion.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((City) resultValue).getCity();
        }
    };
}
