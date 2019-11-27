package com.example.m1j.MainMenuActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.example.m1j.CodeClass.Functions;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class MainMenuActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String user_pic;
    public static String birthday;
    public static String token;


    DatabaseReference rootref;

    BillingProcessor billingProcessor;

    public static boolean purduct_purchase=false;


    public static String action_type="none";
    public static String receiverid="none";
    public static String title="none";
    public static String Receiver_pic="none";

    public  static  MainMenuActivity mainMenuActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mainMenuActivity=this;
        // здесь мы сделаем статическую переменную информации о пользователе, которая будет требоваться в разных
        // местах приложения во время использования
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Variables.uid, "null");
        user_name = sharedPreferences.getString(Variables.f_name, "") + " " +
                sharedPreferences.getString(Variables.l_name, "");
        birthday=sharedPreferences.getString(Variables.birth_day,"");
        user_pic=sharedPreferences.getString(Variables.u_pic,"null");
        token=sharedPreferences.getString(Variables.device_token,"null");
        rootref= FirebaseDatabase.getInstance().getReference();


        if(getIntent().hasExtra("action_type")){
            action_type=getIntent().getExtras().getString("action_type");
            receiverid=getIntent().getExtras().getString("receiverid");
            title=getIntent().getExtras().getString("title");
            Receiver_pic=getIntent().getExtras().getString("icon");
        }



        billingProcessor = new BillingProcessor(this, Variables.licencekey, this);
        billingProcessor.initialize();


        if (savedInstanceState == null) {

            initScreen();

        } else {
            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
        }


        // получаем версию запущенного приложения
        // Рассчитывал, что к этому моменту их будет больше чем одна. И был еще целый класс для наката обновлений
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Variables.versionname=packageInfo.versionName;


    }


    //Инициализация фрагмента
    private void initScreen() {
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(MainMenuActivity.this);
            }
        });
    }

    // при запуске мы сохраним последний токен в firebase
    @Override
    protected void onStart() {
        super.onStart();
        rootref.child("Users").child(user_id).child("token").setValue(token);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Check_version();
    }

    //Обработка перехода назад
    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }






    // ниже все методы нужны, чтобы получить информацию, что пользователь подписался на наше приложение или нет
    //  имейте в виду, что это слушатель, поэтому мы закроем слушателя после проверки на инициализированный метод биллинга
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        purduct_purchase=true;
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        if(billingProcessor.loadOwnedPurchasesFromGoogle()){
            if(billingProcessor.isSubscribed(Variables.product_ID)){
                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
                purduct_purchase=true;
                billingProcessor.release();
            }else {
                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,false).commit();
                purduct_purchase=false;
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }



}
