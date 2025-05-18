package com.example.tideout;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {
    @GET("forecast/oceanography/daily/hp-daily-sea-forecast-day{idDay}.json")
    Call<SeaForecast> getSeaForecast(@Path("idDay") int day, @Query("globalIdLocal") int globalId);
}