package com.example.tideout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoriteBeachesAdapter extends RecyclerView.Adapter<FavoriteBeachesAdapter.ViewHolder> {

    private Context context;
    private List<ResultModel> favoriteBeaches;

    public FavoriteBeachesAdapter(Context context, List<ResultModel> favoriteBeaches) {
        this.context = context;
        this.favoriteBeaches = favoriteBeaches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_beaches, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return favoriteBeaches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBeachName, textViewLocation, textViewForecastDate, textViewSeaTemp, textViewWaveDirection, textViewWaveHeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBeachName = itemView.findViewById(R.id.textViewBeachName);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewForecastDate = itemView.findViewById(R.id.textViewForecastDate);
            textViewSeaTemp = itemView.findViewById(R.id.textViewSeaTemp);
            textViewWaveDirection = itemView.findViewById(R.id.textViewWaveDirection);
            textViewWaveHeight = itemView.findViewById(R.id.textViewWaveHeight);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResultModel beach = favoriteBeaches.get(position);

        holder.textViewBeachName.setText(beach.getName());
        holder.textViewLocation.setText(beach.getRegion());
        holder.textViewForecastDate.setText("Data: " + (beach.getForecastDate() != null ? beach.getForecastDate() : "--"));
        holder.textViewSeaTemp.setText("Temperatura do mar: " + (beach.getSstMin() != 0 ? beach.getSstMin() + "°C - " + beach.getSstMax() + "°C" : "--"));
        holder.textViewWaveDirection.setText("Direção das ondas: " + (beach.getPredWaveDir() != null ? beach.getPredWaveDir() : "--"));
        holder.textViewWaveHeight.setText("Altura das ondas: " + (beach.getTotalSeaMin() != 0 ? beach.getTotalSeaMin() + "m - " + beach.getTotalSeaMax() + "m" : "--"));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BeachDetail.class);
            intent.putExtra("objectid", beach.getId());
            context.startActivity(intent);
        });
    }

}
