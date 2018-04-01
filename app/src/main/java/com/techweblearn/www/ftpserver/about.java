package com.techweblearn.www.ftpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Element version=new Element();
        version.setTitle("Version-: 1.0 ");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getResources().getString(R.string.app_description))
                .addItem(version)
                .addGroup("Connect with us")
                .addEmail("purikunal22@gmail.com")
                .addPlayStore("com.techweblearn.www.ftpserver")
                .addGitHub("kunal52")
                .create();

        setContentView(aboutPage);

    }
}
