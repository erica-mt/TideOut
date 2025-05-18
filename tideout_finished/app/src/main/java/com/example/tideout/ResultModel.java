package com.example.tideout;

public class ResultModel {
    private int id;
    private String name, region, forecastDate, predWaveDir;
    private float latitude, longitude;
    private double sstMin, sstMax, totalSeaMin, totalSeaMax;

    public ResultModel(int id, String name, String region, float latitude, float longitude) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRegion() { return region; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }

    public String getForecastDate() {
        return forecastDate;
    }

    public double getSstMin() {
        return sstMin;
    }

    public double getSstMax() {
        return sstMax;
    }

    public String getPredWaveDir() {
        return predWaveDir;
    }

    public double getTotalSeaMin() {
        return totalSeaMin;
    }

    public double getTotalSeaMax() {
        return totalSeaMax;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    public void setSstMin(double sstMin) {
        this.sstMin = sstMin;
    }

    public void setSstMax(double sstMax) {
        this.sstMax = sstMax;
    }

    public void setPredWaveDir(String predWaveDir) {
        this.predWaveDir = predWaveDir;
    }

    public void setTotalSeaMin(double totalSeaMin) {
        this.totalSeaMin = totalSeaMin;
    }

    public void setTotalSeaMax(double totalSeaMax) {
        this.totalSeaMax = totalSeaMax;
    }


}
