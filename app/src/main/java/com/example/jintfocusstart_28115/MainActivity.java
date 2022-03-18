package com.example.jintfocusstart_28115;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    private final Map<Integer, String> map = new HashMap<>();
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RequestStatus requestStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewValute);
        EditText textInputEditText = findViewById(R.id.textInputEditRus);
        textInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textInputEditText.clearFocus();
            }
            return false;
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        if (WorkWithDate.checkNeedUpdate(sharedPreferences)) {
            new Thread(() -> {
                loadJSONFromURL(JSON_URL);
                while (true) {
                    if (!requestStatus.equals(RequestStatus.REQUESTED)) break;
                }
                if (requestStatus.equals(RequestStatus.ERROR)) {
                    loadValuteFromJSON(sharedPreferences.getString("response", ""));
                } else WorkWithDate.saveDate(editor);
            }).start();
        } else {
            loadValuteFromJSON(sharedPreferences.getString("response", ""));
        }
    }

    private void loadJSONFromURL(String url) {
        requestStatus = RequestStatus.REQUESTED;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    editor.putString("response", response);
                    editor.apply();
                    loadValuteFromJSON(response);
                    requestQueue.getCache().clear();
                },
                error -> {
                    requestStatus = RequestStatus.ERROR;
                    runOnUiThread(() -> Toast.makeText(this, "Ошибка подключения", Toast.LENGTH_SHORT).show());
                }
        );
        requestQueue.add(stringRequest);
    }

    private void loadValuteFromJSON(String response) {
        try {
            JSONObject root = new JSONObject(response);
            JSONObject valute = root.getJSONObject("Valute");
            ArrayList<JSONObject> listItems = new ArrayList<>();
            Iterator<String> keys = valute.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                listItems.add(valute.getJSONObject(key));
                map.put(
                        listItems.size() - 1,
                        valute.getJSONObject(key).getString("Value") +
                                ", " +
                                valute.getJSONObject(key).getString("Nominal")
                );
            }
            ListViewAdapter adapter = new ListViewAdapter(getApplicationContext(),
                    R.layout.row, R.id.textViewNameValute,
                    listItems
            );
            runOnUiThread(() -> listView.setAdapter(adapter));
            requestStatus = RequestStatus.ACCEPTED;
        } catch (JSONException e) {
            requestStatus = RequestStatus.ERROR;
            runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show());
        }
    }

    public void onButtonConversionClick(View view) {
        TextInputEditText result = findViewById(R.id.textInputEditOth);
        TextInputEditText numberRubles = findViewById(R.id.textInputEditRus);
        if (numberRubles.length() != 0 && map.size() != 0) {
            ListViewAdapter arr = (ListViewAdapter) listView.getAdapter();
            String[] mapSplit = map.get(arr.getSelectedPosition()).split(", ");
            BigDecimal rubles = new BigDecimal((numberRubles.getText()).toString());
            BigDecimal value = new BigDecimal(mapSplit[0]);
            BigDecimal nominal = new BigDecimal(mapSplit[1]);
            result.setText(String.valueOf(
                    rubles.multiply(nominal).divide(value, 4, 4))
            );
        } else Toast.makeText(this, "Ошибка конвертации", Toast.LENGTH_SHORT).show();
    }

    public void onButtonUpdateClick(View view) {
        loadJSONFromURL(JSON_URL);
    }
}