package ru.volt.demovolt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.volt.demovolt.AppConfig;
import ru.volt.demovolt.R;
import ru.volt.demovolt.RecordType;

/**
 * Created by dave on 09.05.16.
 */
public class MyAdapter extends ArrayAdapter<RecordType> {
    private RecordType[] values;

    private TextView tvText;
    private ImageView ivFavorite;

    public MyAdapter(RecordType[] values) {
        super(AppConfig.getContext(), R.layout.row_layout, values);
        this.values = values;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) AppConfig.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        tvText = (TextView)rowView.findViewById(R.id.tvText);
        ivFavorite = (ImageView)rowView.findViewById(R.id.ivFavorite);

        RecordType record = values[position];
        tvText.setText(record.getTitle());

        if (record.getFavorites()) {
            ivFavorite.setImageResource(R.drawable.star_on);
        } else {
            ivFavorite.setImageResource(R.drawable.star_off);
        }

        return rowView;
    }
}
