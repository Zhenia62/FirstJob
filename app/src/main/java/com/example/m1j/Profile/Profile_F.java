package com.example.m1j.Profile;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.MainMenuActivity.relateToFragment_OnBack.RootFragment;
import com.example.m1j.Profile.EditProfile.EditProfile_F;
import com.example.m1j.Profile.Profile_Details.Profile_Details_F;
import com.example.m1j.R;
import com.example.m1j.Settings.Setting_F;
import com.squareup.picasso.Picasso;

public class Profile_F extends RootFragment {

    View view;
    Context context;

     public static ImageView profile_image;
      TextView user_name;
      public static TextView age;


    LinearLayout setting_layout,edit_profile_layout;



    public Profile_F() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        context=getContext();


        edit_profile_layout=view.findViewById(R.id.edit_profile_layout);
        edit_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editprofile();
            }
        });



        setting_layout=view.findViewById(R.id.setting_layout);
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting_profile();
            }
        });


        profile_image=view.findViewById(R.id.profile_image);
        user_name=view.findViewById(R.id.user_name);
        age=view.findViewById(R.id.age);


        // отображение фотографии, возраста и имени пользователя
        Picasso.with(context).load(MainMenuActivity.user_pic)
                .resize(200,200)
                .centerCrop()
                .into(profile_image);

        user_name.setText(MainMenuActivity.user_name);
        age.setText(" "+MainMenuActivity.birthday);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_detail();
            }
        });



        return view;
    }


    // открытие окна детальной информации профиля
    // код закомментирован, потому что там вылетал exeption, сейчас уже не помню почему
    public void Profile_detail(){

//        Profile_Details_F profile_details_f = new Profile_Details_F();
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
//        transaction.addToBackStack(null);
//        transaction.replace(R.id.MainMenuFragment, profile_details_f).commit();

    }



    //открытие окна изменения профиля
    public void Editprofile(){
        EditProfile_F editProfile_f = new EditProfile_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, editProfile_f).commit();
    }


    // открытие окна настроек профиля
    public void Setting_profile(){
        Setting_F setting_f = new Setting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, setting_f).commit();
    }





}
