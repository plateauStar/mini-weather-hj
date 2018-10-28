package cn.edu.sspku.hj.miniweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.edu.sspku.hj.bean.City;

public class SearchAdapter extends ArrayAdapter<City> {
    private int resourceId;




    public SearchAdapter(@NonNull Context context, int resource, @NonNull List<City> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        City city = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        } else {
            view = convertView;
        }
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.list_item_title_layout);
        linearLayout.setVisibility(View.GONE);
        TextView textView = view.findViewById(R.id.list_city);
        textView.setText(city.getCity());

        return view;
    }

}

