package com.example.amst_proyecto.data;

public class Coordenada {
    private double latitude;
    private double longitude;
    private String nombreBus;

    public Coordenada() {
    }

    public Coordenada(double latitude, double longitude, String nombreBus) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nombreBus = nombreBus;
    }



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNombreBus() {
        return nombreBus;
    }

    public void setNombreBus(String nombreBus) {
        this.nombreBus = nombreBus;
    }

    @Override
    public String toString() {
        return "Coordenada{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", nombreBus='" + nombreBus + '\'' +
                '}';
    }
}
