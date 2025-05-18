package com.example.tideout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText editTextSearch;
    Button buttonSearchbyName, buttonSearchbyRegion, buttonSearchNearMe;
    RecyclerView results;
    ResultAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = findViewById(R.id.searchInput);
        buttonSearchbyName = findViewById(R.id.btnSearchbyBeachName);
        buttonSearchbyRegion = findViewById(R.id.btnSearchbyBeachRegion);
        buttonSearchNearMe = findViewById(R.id.btnSearchNearMe);
        results = findViewById(R.id.results);

        dbHelper = new DatabaseHelper(this);
        dbHelper.copyDatabase();

        if (results.getLayoutManager() == null) {
            results.setLayoutManager(new LinearLayoutManager(this));
        }

        buttonSearchbyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByName();
            }
        });

        buttonSearchbyRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByRegion();
            }
        });

        buttonSearchNearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { searchNearMe(); }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(results.getContext(),
                DividerItemDecoration.VERTICAL);

        results.addItemDecoration(dividerItemDecoration);

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void searchByName() {
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.isEmpty()) {
            return;
        }

        List<ResultModel> searchResults = dbHelper.searchbyName(searchText);

        if (searchResults.isEmpty()) {
            findViewById(R.id.noResultsMessage).setVisibility(View.VISIBLE);
            findViewById(R.id.results).setVisibility(View.GONE);
        } else {
            findViewById(R.id.noResultsMessage).setVisibility(View.GONE);
            findViewById(R.id.results).setVisibility(View.VISIBLE);
            adapter = new ResultAdapter(this, searchResults);
            results.setAdapter(adapter);
        }
    }

    private void searchByRegion() {
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.isEmpty()) {
            return;
        }

        List<ResultModel> searchResults = dbHelper.searchbyRegion(searchText);
        adapter = new ResultAdapter(this, searchResults);
        results.setAdapter(adapter);
    }

    private void searchNearMe() {
        Intent intent = new Intent(SearchActivity.this, LocationActivity.class);
        startActivity(intent);
    }

}

