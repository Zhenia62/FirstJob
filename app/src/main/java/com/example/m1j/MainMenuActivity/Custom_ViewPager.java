package com.example.m1j.MainMenuActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

//Свой экземпляр ViewPager
//Понадобился, просто для большей гибкости
//Тут перечилсены методы-handlers для обработки некоторых событий
public class Custom_ViewPager extends ViewPager {
    private boolean enabled;

    public Custom_ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
