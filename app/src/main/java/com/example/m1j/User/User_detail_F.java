package com.example.m1j.User;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.m1j.CodeClass.Functions;
import com.example.m1j.CodeClass.Variables;
import com.example.m1j.MainMenuActivity.MainMenuActivity;
import com.example.m1j.MainMenuActivity.relateToFragment_OnBack.RootFragment;
import com.example.m1j.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



//  в этом классе мы получим все данные конкретного пользователя
public class User_detail_F extends RootFragment implements View.OnClickListener {


    View view;
    Context context;

    ImageButton move_downbtn;

    RelativeLayout username_layout;
    ScrollView scrollView;

    TextView username_txt, bottom_age,bottom_job_txt,bottom_school_txt,bottom_location_text,bottom_about_txt;

    TextView bottom_report_txt;

    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;

    Nearby_User_Get_Set data_item;

    IOSDialog iosDialog;

    ImageButton profile_menu;

    public User_detail_F() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_detail_, container, false);
        context = getContext();

        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            // получить все данные пользователя из предыдущего фрагмента и показать его здесь
         data_item= (Nearby_User_Get_Set) bundle.getSerializable("data");
        }

        scrollView = view.findViewById(R.id.scrollView);
        username_layout = view.findViewById(R.id.username_layout);

        move_downbtn = view.findViewById(R.id.move_downbtn);
        move_downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getActivity().onBackPressed();


            }
        });




       sliderLayout = view.findViewById(R.id.slider);
       pagerIndicator=view.findViewById(R.id.custom_indicator);

       sliderLayout.setVisibility(View.INVISIBLE);


        YoYo.with(Techniques.BounceInDown)
                .duration(800)
                .playOn(move_downbtn);

        init_bottom_view();


        return view;

    }


    // этот метод инициализирует все представления и устанавливает данные в этом представлении
    public void init_bottom_view() {

        profile_menu=view.findViewById(R.id.profile_menu);
        profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v);
            }
        });


        username_txt = view.findViewById(R.id.username_txt);
        username_txt.setText(data_item.getName());


        bottom_age = view.findViewById(R.id.bottom_age);


        if(!data_item.getBirthday().equals(""))
        bottom_age.setText(", "+data_item.getBirthday());


        bottom_job_txt=view.findViewById(R.id.bottom_job_txt);
        if(data_item.getJob_title().equals("") & !data_item.getCompany().equals("")){

            bottom_job_txt.setText(data_item.getJob_title());

        }else if(data_item.getCompany().equals("") && !data_item.getJob_title().equals("") ){

            bottom_job_txt.setText(data_item.getJob_title());

        }
        else if(data_item.getCompany().equals("") && data_item.getJob_title().equals("") ){
            view.findViewById(R.id.job_layout).setVisibility(View.GONE);
        }
        else {
            bottom_job_txt.setText(data_item.getJob_title()+" at "+data_item.getSchool());

        }




        bottom_location_text=view.findViewById(R.id.bottom_location_txt);
        bottom_location_text.setText(data_item.getLocation());

        bottom_about_txt=view.findViewById(R.id.bottom_about_txt);
        if(data_item.getAbout().equals("")){
            bottom_about_txt.setVisibility(View.GONE);
        }
        bottom_about_txt.setText(data_item.getAbout());


        bottom_report_txt=view.findViewById(R.id.bottom_report_txt);
        bottom_report_txt.setText("Report "+data_item.getFirst_name());
        bottom_report_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report_User_alert();
            }
        });
    }


    // когда вся анимация будет выполнена, мы поместим данные в представление
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    // когда анимация будет завершена, мы покажем слайд с изображением
                    // потому что анимация в таком случае будет показывать бегло

                    fill_data();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 200);
        }
    }



    // этот метод установит и покажет слайдер картинок
    public void fill_data(){
        sliderLayout.setCustomIndicator(pagerIndicator);
        sliderLayout.stopAutoCycle();
        ArrayList<String> file_maps=new ArrayList<>() ;
        file_maps=data_item.getImagesurl();
        for (int i = 0; i < file_maps.size(); i++) {

          if(!file_maps.get(i).equals("")){

            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
            defaultSliderView
                    .image(file_maps.get(i))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
            defaultSliderView.bundle(new Bundle());
            sliderLayout.addSlider(defaultSliderView);

            }
        }
        sliderLayout.setVisibility(View.VISIBLE);

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        }
    }


    // это покажет предупреждение, когда мы нажимаем, чтобы сообщить(пожаловаться) о пользователе
    public void Report_User_alert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle("Жалоба")
                .setMessage("Вы уверены, что хотите пожаловаться?")
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Send_report();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }


    // ниже два метода получат всех новых пользователей, которые находятся рядом с нами, и проанализируют данные в dataset
    private void Send_report() {
        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("my_id", MainMenuActivity.user_id);
            parameters.put("fb_id", data_item.getFb_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.flat_user, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        JSONArray jsonArray=response.optJSONArray("msg");
                        Toast.makeText(context, ""+jsonArray.optJSONObject(0).optString("response"), Toast.LENGTH_SHORT).show();

                        iosDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        iosDialog.dismiss();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }



     PopupWindow popup;
    private void displayPopupWindow(View anchorView) {
        popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.item_menu_popup_option, null);

        TextView report=layout.findViewById(R.id.report);
        popup.setContentView(layout);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
               Report_User_alert();
            }
        });


        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView,anchorView.getWidth(),anchorView.getHeight()- (Functions.convertDpToPx(context,60)));

    }




}