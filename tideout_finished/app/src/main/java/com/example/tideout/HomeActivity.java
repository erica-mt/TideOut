package com.example.tideout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private Button searchButton, editAccountButton, favoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchButton = findViewById(R.id.btnSearch);
        editAccountButton = findViewById(R.id.btnEditAccount);
        favoritesButton = findViewById(R.id.btnFavorites);

        searchButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
        editAccountButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AccountActivity.class)));
        favoritesButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FavoritesActivity.class)));


    }
}
