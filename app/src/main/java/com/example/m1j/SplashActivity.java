package com.example.m1j;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.m1j.Account.Enable_location_F;
import com.example.m1j.Account.LoginActivity;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class SplashActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);




//         Проверка на наличие текущего пользователя в системе
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                    // если пользователь уже вошел в систему, то мы получаем текущее местоположение пользователя
                    if (getIntent().hasExtra("action_type")) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        String action_type = getIntent().getExtras().getString("action_type");
                        String receiverid = getIntent().getExtras().getString("senderid");
                        String title = getIntent().getExtras().getString("title");
                        String icon = getIntent().getExtras().getString("icon");

                        intent.putExtra("icon", icon);
                        intent.putExtra("action_type", action_type);
                        intent.putExtra("receiverid", receiverid);
                        intent.putExtra("title", title);


                        startActivity(intent);
                        finish();
                    }else
                   GPSStatus();
                } else {

                    // иначе мы переместим пользователя на экран входа в систему
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();

                }
            }
        }, 2000);


    }


//     Получаем статус Gps. Метод проверяет включено ли отслеживание навигации или нет.
    public void GPSStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            //  если gps не включен, то мы перейдем к экрану его настройки
            Toast.makeText(this, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);

        } else {

            // если оно включено, то получаем местоположение пользователя и сохраняем его в локальной бд
            GetCurrentlocation();
        }
   }


        // если Gps успешно включен, то мы снова проверим статус Gps
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 2) {
                GPSStatus();
            }
        }


        private FusedLocationProviderClient mFusedLocationClient;

        private void GetCurrentlocation () {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // здесь, если пользователь не дал разрешение на включение(отслеживаиние) местоположения, то мы переводим пользователя на экран влючения данных настроек
                return;
            }

            // иначе мы получим местоположение и сохраним его в локальном хранилище и перейдем на главный экран
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Берем последнее известное место. В некоторых редких ситуациях вылетал null, не особо понял почему -> сделал проверку.
                            if (location != null) {

                                // Опять же  если все хорошо и мы получим местоположение то сохраним его и перейдем на главный экран
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Variables.current_Lat, "" + location.getLatitude());
                                editor.putString(Variables.current_Lon, "" + location.getLongitude());
                                editor.commit();
                                startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            } else {
                                // в противном случае мы будем использовать дефолтное местоположение
                                //По дефолту установил рязань
                                if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Variables.current_Lat, "54.6269");
                                    editor.putString(Variables.current_Lon, "39.6916");
                                    editor.commit();
                                }
                                startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();
                            }
                        }
                    });
        }


        // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
        private void enable_location () {
            Enable_location_F enable_location_f = new Enable_location_F();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            getSupportFragmentManager().popBackStackImmediate();
            transaction.replace(R.id.Login_Activ, enable_location_f).addToBackStack(null).commit();

        }


}
