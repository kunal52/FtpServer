package com.techweblearn.www.ftpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;

public class HowToUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getResources().getString(R.string.how_to_use))
                .addGroup("Connect with us")
                .addEmail("purikunal22@gmail.com")
                .create();

        setContentView(aboutPage);
    }
}
