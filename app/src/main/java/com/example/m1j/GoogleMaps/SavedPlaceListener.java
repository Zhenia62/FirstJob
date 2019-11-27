package com.example.m1j.GoogleMaps;

import java.util.ArrayList;

//Слушатель изменения
//Нужен для обработки нажатия по какому-либо метсу на карте (onSavedPlaceClick в SearchPlaces)
interface SavedPlaceListener {
    public void onSavedPlaceClick(ArrayList<SavedAddress> mResultList, int position);
}
