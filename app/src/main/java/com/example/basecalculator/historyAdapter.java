package com.example.basecalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class historyAdapter extends ArrayAdapter<history> {

    private Context mContext;
    private int mResource;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView value;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public historyAdapter(Context context, int resource, ArrayList<history> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String value = getItem(position).getValue();
        String base = getItem(position).getBase();

        history history = new history(value,base);

        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.value = (TextView) convertView.findViewById(R.id.history_text);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.value.setText(history.getValue()+" ("+history.getBase()+")");

        return convertView;
    }
}