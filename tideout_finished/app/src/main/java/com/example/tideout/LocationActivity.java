package com.example.tideout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseHelper dbHelper;
    private List<ResultModel> allBeaches;
    private TextView txtLatitude, txtLongitude, txtEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        txtEstado = findViewById(R.id.txtEstado);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        dbHelper = new DatabaseHelper(this);
        allBeaches = dbHelper.getAllBeaches();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                double latitude = 38.716848;
                double longitude = -9.420762;

                // double latitude = location.getLatitude();
                // double longitude = location.getLongitude();

                txtLatitude.setText(String.format("Latitude: %.6f", latitude));
                txtLongitude.setText(String.format("Longitude: %.6f", longitude));
                txtEstado.setText("Localização obtida com sucesso");

                try {
                    List<ResultModel> nearbyBeaches = getBeachesInRadius(latitude, longitude);

                    if (nearbyBeaches.isEmpty()) {
                        txtEstado.setText("Parece que não há praias próximas. 😢");
                    } else {
                        displayNearbyBeaches(nearbyBeaches);
                    }
                } catch (Exception e) {
                    txtEstado.setText("Erro ao buscar praias.");
                }
            }
        };
        verificarPermissoes();

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<ResultModel> getBeachesInRadius(double userLat, double userLon) {
        List<ResultModel> nearbyBeaches = new ArrayList<>();

        for (ResultModel beach : allBeaches) {
            double distance = calculateDistance(userLat, userLon, beach.getLatitude(), beach.getLongitude());
            if (distance <= 5) {
                nearbyBeaches.add(beach);
            }
        }

        displayNearbyBeaches(nearbyBeaches);
        return nearbyBeaches;
    }

    public void displayNearbyBeaches(List<ResultModel> nearbyBeaches) {
        RecyclerView recyclerView = findViewById(R.id.results);
        ResultAdapter adapter = new ResultAdapter(this, nearbyBeaches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void verificarPermissoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else {
            iniciarGPS();
        }
    }

    private void iniciarGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            txtEstado.setText("A processar localização...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public void goBack(View view) {
        finish();
    }
}
