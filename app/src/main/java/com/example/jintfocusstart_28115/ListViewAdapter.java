package com.example.jintfocusstart_28115;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    private final int listLayout;
    private final List<JSONObject> usersList;
    private final Context context;
    private int selectedPosition = 0;

    public ListViewAdapter(Context context, int listLayout,
                           int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout = listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder")
        View listViewItem = inflater.inflate(listLayout, null, false);

        RadioButton nameValute = listViewItem.findViewById(R.id.textViewNameValute);
        try {
            BigDecimal value = new BigDecimal(usersList.get(position).getString("Value"));
            BigDecimal nominal = new BigDecimal(usersList.get(position).getString("Nominal"));
            String valueString = usersList.get(position).getString("Name") +
                    ": " + value.divide(nominal, 4, 4);
            nameValute.setText(valueString);
            nameValute.setChecked(position == selectedPosition);
            nameValute.setTag(position);
            nameValute.setOnClickListener(view -> {
                selectedPosition = (Integer) view.getTag();
                notifyDataSetChanged();
            });
        } catch (JSONException je) {
            Toast.makeText(context, "Ошибка загрузки списка валют", Toast.LENGTH_SHORT).show();
        }
        return listViewItem;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

}
