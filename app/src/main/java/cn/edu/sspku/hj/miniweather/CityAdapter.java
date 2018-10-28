package cn.edu.sspku.hj.miniweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.edu.sspku.hj.bean.City;

public class CityAdapter extends ArrayAdapter<City> {
    private int resourceId;
    private AlphabetIndexer mIndexer;

    public void setmIndexer(AlphabetIndexer mIndexer) {
        this.mIndexer = mIndexer;
    }

    public CityAdapter(@NonNull Context context, int resource, @NonNull List<City> objects) {
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
        LinearLayout itemFirstLayout = (LinearLayout) view.findViewById(R.id.list_item_title_layout);
        TextView itemFirstAlpha = view.findViewById(R.id.list_item_first);
//        TextView itemProvince = view.findViewById(R.id.list_province);
        TextView itemCity = view.findViewById(R.id.list_city);
        int mSection = mIndexer.getSectionForPosition(position);
        if(position == mIndexer.getPositionForSection(mSection)) {
            itemFirstAlpha.setText(city.getFirstPY());
            itemFirstLayout.setVisibility(View.VISIBLE);
        }
        else {
            itemFirstLayout.setVisibility(View.GONE);
        }
        itemCity.setText(city.getCity());
//        itemProvince.setText(city.getProvince());
        Log.d("adapter",city.getFirstPY()+city.getCity());
        return view;
    }

}

