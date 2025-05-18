package com.example.tideout;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeachDetail extends AppCompatActivity {

    DatabaseHelper dbHelper;
    TextView textViewNome, tableQualidadeAgua, tableVigilancia, textViewForecast, tableSocorro, tableSanitarios, textViewAverage;
    TableLayout tableLayout;
    ImageView favoriteStar;
    boolean isFavorite;
    DatabaseReference databaseBeachVotes, userVotesRef, databaseCrowdVotes;
    Button btnGreen, btnYellow, btnRed, btnLow, btnMedium, btnHigh;
    String currentDate;
    FirebaseUser user;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        textViewNome = findViewById(R.id.textViewNome);
        tableQualidadeAgua = findViewById(R.id.tableQualidadeAgua);
        tableVigilancia = findViewById(R.id.tableVigilancia);
        tableSanitarios = findViewById(R.id.tableSanitarios);
        tableSocorro = findViewById(R.id.tableSocorro);
        dbHelper = new DatabaseHelper(this);
        favoriteStar = findViewById(R.id.favoriteStar);
        tableLayout = findViewById(R.id.tableLayout);
        textViewAverage = findViewById(R.id.textView_average);
        btnGreen = findViewById(R.id.btn_green);
        btnYellow = findViewById(R.id.btn_yellow);
        btnRed = findViewById(R.id.btn_red);
        btnLow = findViewById(R.id.btnBaixa);
        btnMedium = findViewById(R.id.btnMedia);
        btnHigh = findViewById(R.id.btnAlta);

        databaseBeachVotes = FirebaseDatabase.getInstance().getReference("beach_votes");
        userVotesRef = FirebaseDatabase.getInstance().getReference("user_beach_votes");
        databaseCrowdVotes = FirebaseDatabase.getInstance().getReference("crowd_votes");

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();

        id = getIntent().getIntExtra("objectid", -1);
        if (id != -1) {
            loadDetails(id);
            checkFavoriteStatus(id);
        }

        favoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(id);
            }
        });

        fetchSeaForecast(0, 1111026);
        loadAverageVotes();
        loadAverageCrowdVotes();
        checkUserAuthentication();

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> finish());

    }

    private void loadDetails(int id) {
        Cursor cursor = dbHelper.getData("SELECT * FROM praias WHERE objectid = " + id);
        if (cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex("nome_praia");
            String name = null;
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex);
            }
            textViewNome.setText(name);

            int waterQualityIndex = cursor.getColumnIndex("qualidade");
            String waterQuality = null;
            if (waterQualityIndex >= 0) {
                waterQuality = cursor.getString(waterQualityIndex);
            }
            tableQualidadeAgua.setText(waterQuality);

            int vigilanciaIndex = cursor.getColumnIndex("vigilancia");
            int vigilancia = cursor.getInt(vigilanciaIndex);
            if (vigilancia == 1) {
                tableVigilancia.setText("Sim");
            }
            else {
                tableVigilancia.setText("Não");
            }

            int socorroIndex = cursor.getColumnIndex("posto_socorro");
            int socorro = cursor.getInt(socorroIndex);
            if (socorro == 1) {
                tableSocorro.setText("Sim");
            }
            else {
                tableSocorro.setText("Não");
            }

            int sanitariosIndex = cursor.getColumnIndex("sanitarios");
            int sanitarios = cursor.getInt(sanitariosIndex);
            if (sanitarios == 1) {
                tableSanitarios.setText("Sim");
            }
            else {
                tableSanitarios.setText("Não");
            }
        }
        cursor.close();
    }

    private void fetchSeaForecast(int day, int globalId) {
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

                        TableLayout tableLayout = findViewById(R.id.tableLayout);
                        tableLayout.removeAllViews();

                        if (selectedItem != null) {
                            addTableRow(tableLayout, "Data", forecast.getForecastDate());
                            addTableRow(tableLayout, "Temperatura do mar", selectedItem.getSstMin() + "°C - " + selectedItem.getSstMax() + "°C");
                            addTableRow(tableLayout, "Direção das ondas", selectedItem.getPredWaveDir());
                            addTableRow(tableLayout, "Altura das ondas", selectedItem.getTotalSeaMin() + "m - " + selectedItem.getTotalSeaMax() + "m");
                        } else {
                            textViewForecast.setText("Sem dados disponíveis para este local (ID: " + globalId + ").");
                        }
                    } else {
                        textViewForecast.setText("Sem dados disponíveis.");
                    }
                } else {
                    textViewForecast.setText("Falha ao obter dados.");
                }
            }

            @Override
            public void onFailure(Call<SeaForecast> call, Throwable t) {
                Toast.makeText(BeachDetail.this, "Falha ao carregar estado do tempo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTableRow(TableLayout tableLayout, String label, String value) {
        TableRow row = new TableRow(this);

        TextView labelView = new TextView(this);
        labelView.setText(label);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setGravity(Gravity.END);

        row.addView(labelView);
        row.addView(valueView);

        tableLayout.addView(row);
    }

    private void checkFavoriteStatus(int id) {
        isFavorite = dbHelper.isFavorite(id);
        updateStarIcon();
    }

    private void toggleFavorite(int id) {
        if (isFavorite) {
            dbHelper.removeFavorite(id);
            isFavorite = false;
        } else {
            dbHelper.addFavorite(id);
            isFavorite = true;
        }
        updateStarIcon();
    }

    private void updateStarIcon() {
        if (isFavorite) {
            favoriteStar.setImageResource(R.drawable.ic_star_filled);
        } else {
            favoriteStar.setImageResource(R.drawable.ic_star_border);
        }
    }




    // BANDEIRA

    private void checkUserAuthentication() {
        boolean isAuthenticated = false;

        if (user != null) {
            isAuthenticated = true;
        } else {
            Toast.makeText(this, "Deve iniciar sessão para votar.", Toast.LENGTH_SHORT).show();
        }

        btnGreen.setEnabled(isAuthenticated);
        btnYellow.setEnabled(isAuthenticated);
        btnRed.setEnabled(isAuthenticated);
        btnHigh.setEnabled(isAuthenticated);
        btnMedium.setEnabled(isAuthenticated);
        btnLow.setEnabled(isAuthenticated);

        if (isAuthenticated) {
            btnGreen.setOnClickListener(v -> submitVote("green"));
            btnYellow.setOnClickListener(v -> submitVote("yellow"));
            btnRed.setOnClickListener(v -> submitVote("red"));
            btnHigh.setOnClickListener(v -> {
                Log.d("ButtonLog", "Button clicked, voteType: high");
                submitCrowdVote("high");
            });
            btnMedium.setOnClickListener(v -> submitCrowdVote("medium"));
            btnLow.setOnClickListener(v -> submitCrowdVote("low"));
        }
    }
    private void submitVote(String voteType) {
        if (user == null) {
            Toast.makeText(this, "Deve iniciar sessão para votar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        if (user != null) {
            String date = getCurrentDate();

            DatabaseReference userVoteRef = FirebaseDatabase.getInstance()
                    .getReference("user_beach_votes")
                    .child(userId)
                    .child(String.valueOf(id))
                    .child(date);

            userVoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(BeachDetail.this, "Já votou nesta praia hoje.", Toast.LENGTH_SHORT).show();
                    } else {
                        recordVote(voteType);
                        userVoteRef.setValue(voteType);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Database error: " + error.getMessage());
                }
            });

        } else {
            Log.e("Firebase", "User not authenticated");
        }
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void recordVote(String voteType) {
        DatabaseReference voteRef = databaseBeachVotes.child(String.valueOf(id)).child(currentDate).child(voteType);

        voteRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;
                }
                mutableData.setValue(currentCount + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (error == null) {
                    Toast.makeText(BeachDetail.this, "Voto registado!", Toast.LENGTH_SHORT).show();
                    loadAverageVotes(); // Refresh the average
                } else {
                    Toast.makeText(BeachDetail.this, "Erro ao registar voto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadAverageVotes() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DatabaseReference beachRef = FirebaseDatabase.getInstance()
                .getReference("beach_votes")
                .child(String.valueOf(id))
                .child(currentDate);

        beachRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int greenCount = 0, yellowCount = 0, redCount = 0;

                if (snapshot.child("green").exists()) {
                    greenCount = snapshot.child("green").getValue(Integer.class);
                }
                if (snapshot.child("yellow").exists()) {
                    yellowCount = snapshot.child("yellow").getValue(Integer.class);
                }
                if (snapshot.child("red").exists()) {
                    redCount = snapshot.child("red").getValue(Integer.class);
                }

                btnGreen.setText("Verde (" + greenCount + ")");
                btnYellow.setText("Amarela (" + yellowCount + ")");
                btnRed.setText("Vermelha (" + redCount + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }








    // LOTAÇÃO

    private void submitCrowdVote(String voteType) {
        if (user == null) {
            Toast.makeText(this, "Deve iniciar sessão para votar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        if (user != null) {
            String date = currentDate;

            DatabaseReference userVoteRef = FirebaseDatabase.getInstance()
                    .getReference("user_crowd_votes")
                    .child(userId)
                    .child(String.valueOf(id))
                    .child(date);

            userVoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(BeachDetail.this, "Já votou nesta praia hoje!", Toast.LENGTH_SHORT).show();
                    } else {
                        recordCrowdVote(voteType);
                        userVoteRef.setValue(voteType);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Database error: " + error.getMessage());
                }
            });

        } else {
            Log.e("Firebase", "User not authenticated");
        }
    }

    private void recordCrowdVote(String voteType) {
        if (user == null) {
            Toast.makeText(this, "Deve iniciar sessão para votar!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference voteRef = databaseCrowdVotes.child(String.valueOf(id)).child(currentDate).child(voteType);

        if (voteType == null || String.valueOf(id) == null || currentDate == null) {
            Log.e("Firebase", "One or more values are null: voteType=" + voteType + ", id=" + id + ", currentDate=" + currentDate);
            return;
        }

        voteRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    currentCount = 0;
                }
                mutableData.setValue(currentCount + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (error == null) {
                    Toast.makeText(BeachDetail.this, "Voto registado!", Toast.LENGTH_SHORT).show();
                    loadAverageCrowdVotes();
                } else {
                    Toast.makeText(BeachDetail.this, "Erro ao registar voto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadAverageCrowdVotes() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DatabaseReference beachRef = FirebaseDatabase.getInstance()
                .getReference("crowd_votes")
                .child(String.valueOf(id))
                .child(currentDate);

        beachRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int lowCount = 0, mediumCount = 0, highCount = 0;

                if (snapshot.child("low").exists()) {
                    lowCount = snapshot.child("low").getValue(Integer.class);
                }
                if (snapshot.child("medium").exists()) {
                    mediumCount = snapshot.child("medium").getValue(Integer.class);
                }
                if (snapshot.child("high").exists()) {
                    highCount = snapshot.child("high").getValue(Integer.class);
                }

                btnLow.setText("Baixa (" + lowCount + ")");
                btnMedium.setText("Média (" + mediumCount + ")");
                btnHigh.setText("Alta (" + highCount + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }


}
