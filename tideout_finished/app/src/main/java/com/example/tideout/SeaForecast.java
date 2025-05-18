package com.example.tideout;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeaForecast {
    @SerializedName("forecastDate")
    private String forecastDate;

    @SerializedName("data")
    private List<SeaForecastItem> data;

    public String getForecastDate() { return forecastDate; }
    public List<SeaForecastItem> getData() { return data; }
}