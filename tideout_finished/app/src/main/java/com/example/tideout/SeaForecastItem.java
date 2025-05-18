package com.example.tideout;

public class SeaForecastItem {

    private int globalIdLocal;
    private String sstMin; //mínimo diário da temperatura da superfície do mar em ºC
    private String sstMax; //máximo diário da temperatura da superfície do mar em ºC
    private String predWaveDir;  //rumo predominante da onda (N, NE, E, SE, S, SW, W, NW)
    private String totalSeaMin; //mínimo diário da altura significativa das ondas, em metros
    private String totalSeaMax; //máximo diário da altura significativa das ondas, em metros

    public int getGlobalIdLocal() {
        return globalIdLocal;
    }
    public String getSstMin() {
        return sstMin;
    }
    public String getSstMax() {
        return sstMax;
    }
    public String getPredWaveDir() {
        return predWaveDir;
    }
    public String getTotalSeaMin() { return totalSeaMin; }
    public String getTotalSeaMax() { return totalSeaMax; }

    public void setGlobalIdLocal(int globalIdLocal) {
        this.globalIdLocal = globalIdLocal;
    }
}