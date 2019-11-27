package com.example.m1j.GoogleMaps;

//Библиотека классов, сохранения текущего адреса в системе. Точне его получение и установка, черз обыче гет и сет. (Не все методы вообще используются)
public class SavedAddress {
    String Latitude, Longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
