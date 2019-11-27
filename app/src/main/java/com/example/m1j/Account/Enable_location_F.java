package com.example.m1j.Account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.m1j.CodeClass.Variables;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import static android.content.Context.MODE_PRIVATE;


public class Enable_location_F extends Fragment {


    View view;
    Context context;
    Button enable_location_btn;
    SharedPreferences sharedPreferences;
    IOSDialog iosDialog;

    public Enable_location_F() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_enable_location, container, false);
        context = getContext();


        //Экземпляр диалоговоого окна
        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        sharedPreferences = context.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);


        //Получение геолокации
        enable_location_btn = view.findViewById(R.id.enable_location_btn);
        enable_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();

            }
        });


        return view;
    }

    //Метод, который просто задает анимацию
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim = MoveAnimation.create(MoveAnimation.LEFT, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    // после того как все анимации сделаны мы будем вызывать этот метод для получения местоположения пользователя
                    GPSStatus();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, 300);
        }
    }


    //Ниже два метода, которые вызываются при получении данных о местоположении
    //Первый делает запрос, с передачей информации
    //Второй его обрабатывает
    private void getLocationPermission() {

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                123);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetCurrentlocation();
                } else {
                    Toast.makeText(context, "Пожалуйста, дайте разрешение", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }

    private FusedLocationProviderClient mFusedLocationClient;


    //Получение текущего статуса геолокации
    //Было сделано так, что продолжить работу с выключеным статусом нельзя (нужно было для тестирования)
    public void GPSStatus() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            Toast.makeText(context, "Невозможна работа, без включения геолокации", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
        } else {
            GetCurrentlocation();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            GPSStatus();
        }
    }

    // основной метод, используемый для получения местоположения пользователя
    private void GetCurrentlocation() {
        iosDialog.show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        // Проверка разрешения на доступ к местоположению, если его нет то мы запрашиваем это разрешение
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            iosDialog.cancel();
            getLocationPermission();
            return;
        }

        // Если пользователь дает разрешение, то этот метод будет вызывать и получать текущее местоположение пользователя
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        iosDialog.cancel();
                        if (location != null) {

                            // Сохраняем и переходим на главный экран
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Variables.current_Lat, "" + location.getLatitude());
                            editor.putString(Variables.current_Lon, "" + location.getLongitude());
                            editor.apply();

                            GoToNext_Activty();

                        } else {

                            if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Variables.current_Lat, "54.6269");
                                editor.putString(Variables.current_Lon, "39.6916");
                                editor.apply();
                            }

                            GoToNext_Activty();
                        }
                    }
                });
    }



    //переход на следующую акт.
    //особенность лишь в добавлении некоторых анимаций
    public void GoToNext_Activty() {
        startActivity(new Intent(getActivity(), MainMenuActivity.class));
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        getActivity().finishAffinity();
    }


}

