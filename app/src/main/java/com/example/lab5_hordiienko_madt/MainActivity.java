package com.example.lab5_hordiienko_madt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner ratesFilter;
    ListView listOfRates;

    ArrayList<String> allRates = new ArrayList<>(); //full data
    ArrayList<String> filterOptions = new ArrayList<>(); //filtered view

    ArrayAdapter<String> listAdapter;
    DataLoader dataLoader = new DataLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listOfRates = findViewById(R.id.listOfRates);
        ratesFilter = findViewById(R.id.ratesFilter);

        setupSpinner();
        loadRates();
    }

    public void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_list, //string-array resource
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratesFilter.setAdapter(adapter);
        ratesFilter.setSelection(0); //all

        ratesFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //rates loader
    public void loadRates() {
        new LoadRatesTask().execute();
    }

    //async tasks for loading rates as well as loading the filtering options
    private class LoadRatesTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return dataLoader.loadRates();
        }

        @Override
        protected void onPostExecute(ArrayList<String> rates) {
            allRates = new ArrayList<>(rates);

            //debugger, need to be viewed through logcat for results
            for (String r : filterOptions) {
                System.out.println(r);
            }

            filterOptions.clear();
            filterOptions.addAll(rates);
            setupListView();
            filterList();
        }
    }

    //listview setup function
    public void setupListView() {
        listAdapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                filterOptions
        );
        listOfRates.setAdapter(listAdapter);
    }

    //filtering function with an emdash as a splitter, shows all rates if "all" is chosen
    //to avoid errors, there is the initial allRates list used
    public void filterList() {
        if (listAdapter == null) return;

        String selected = ratesFilter.getSelectedItem().toString().trim();
        ArrayList<String> filtered = new ArrayList<>();

        for (String rate : allRates) {
            String[] parts = rate.split("–"); //split by dash

            if (parts.length < 2) continue; //safety check
            String code = parts[0].trim();

            if (selected.equalsIgnoreCase("All") || code.equalsIgnoreCase(selected)) {
                filtered.add(rate);
            }
        }

        listAdapter.clear();
        listAdapter.addAll(filtered);
        listAdapter.notifyDataSetChanged();
    }
}