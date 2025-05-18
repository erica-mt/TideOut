package com.example.tideout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteBeachesAdapter adapter;
    private List<ResultModel> favoriteBeachesList = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerViewFavoriteBeaches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        loadFavoriteBeaches();

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadFavoriteBeaches() {
        favoriteBeachesList = databaseHelper.getFavoriteBeaches();

        if (favoriteBeachesList == null || favoriteBeachesList.isEmpty()) {
            Toast.makeText(this, "Nenhuma praia encontrada.", Toast.LENGTH_SHORT).show();
        } else {
            for (ResultModel beach : favoriteBeachesList) {
                fetchSeaForecast(0, 1111026, beach);
            }
        }
        adapter = new FavoriteBeachesAdapter(this, favoriteBeachesList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchSeaForecast(int day, int globalId, ResultModel beach) {
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Call<SeaForecast> call = service.getSeaForecast(day, globalId);

        call.enqueue(new Callback<SeaForecast>() {
            @Override
            public void onResponse(Call<SeaForecast> call, Response<SeaForecast> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SeaForecast forecast = response.body();

                    if (forecast.getData() != null && !forecast.getData().isEmpty()) {
                        SeaForecastItem selectedItem = null;

                        for (SeaForecastItem item : forecast.getData()) {
                            if (item.getGlobalIdLocal() == globalId) {
                                selectedItem = item;
                                break;
                            }
                        }

                        if (selectedItem != null) {
                            // Set forecast details to the beach object
                            beach.setForecastDate(forecast.getForecastDate());
                            beach.setSstMin(Double.parseDouble(selectedItem.getSstMin()));
                            beach.setSstMax(Double.parseDouble(selectedItem.getSstMax()));
                            beach.setTotalSeaMin(Double.parseDouble(selectedItem.getTotalSeaMin()));
                            beach.setTotalSeaMax(Double.parseDouble(selectedItem.getTotalSeaMax()));
                            beach.setPredWaveDir(selectedItem.getPredWaveDir());

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<SeaForecast> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this, "Falha ao obter dados para a praia: " + globalId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

