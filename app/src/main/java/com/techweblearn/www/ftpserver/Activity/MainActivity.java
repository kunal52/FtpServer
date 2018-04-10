package com.techweblearn.www.ftpserver.Activity;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.techweblearn.www.ftpserver.HowToUse;
import com.techweblearn.www.ftpserver.Interfaces.MainActivityPresenterInterface;
import com.techweblearn.www.ftpserver.Interfaces.MainActivityViewInterface;
import com.techweblearn.www.ftpserver.R;
import com.techweblearn.www.ftpserver.Service.FtpService;
import com.techweblearn.www.ftpserver.SettingActivity;
import com.techweblearn.www.ftpserver.Utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static com.techweblearn.www.ftpserver.Utils.runAsRoot;

public class MainActivity extends AppCompatActivity implements MainActivityViewInterface, View.OnClickListener
                                                                ,SwitchCompat.OnCheckedChangeListener{

    RelativeLayout relativeLayout;
    TextView ftpaddress;
    Button ftpstartstop;
    MainActivityPresenterInterface mainActivityPresenter;
    LottieAnimationView router_animation;
    Snackbar snackbar;

    private InterstitialAd mInterstitialAd;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ftpaddress = findViewById(R.id.ftpaddress);
        ftpstartstop = findViewById(R.id.ftpcontrol);
        relativeLayout = findViewById(R.id.rootlayout);
        router_animation=findViewById(R.id.router_animation);
        adView = findViewById(R.id.adView);
        router_animation.setProgress(0f);


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstetialAdId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });

        Utils.copyFileFromAsssets("users.properties", this);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        ftpstartstop.setOnClickListener(this);
        Utils.requestPermission(this);
        mainActivityPresenter = new MainActivityPresenter(this, this);
        mainActivityPresenter.checkFtpIsRunning();

        getLocalIpAddress();
        getMobileIPAddress();
    }

    @Override
    public void onClick(View view) {
        if (Utils.isMyServiceRunning(FtpService.class, this)) {
            mainActivityPresenter.onftpStartStop();
            return;
        }


       /* if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isMobile",false))
        {
            mainActivityPresenter.onftpStartStop();
            snackbar=Snackbar.make(relativeLayout, "Ftp is enable Go to About in Setting To see the IP", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
            snackbar.show();

            return;

        }*/

        if (Utils.isWifiApEnable(MainActivity.this)||Utils.isWifiEnable(MainActivity.this)) {
            Log.d("Kunal","Is Service Running");
            mainActivityPresenter.onftpStartStop();
        } else {

            snackbar=Snackbar.make(relativeLayout, "Enable WifiHotspot or Connect To Wifi", Snackbar.LENGTH_LONG)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
            snackbar.show();

        }
    }

    @Override
    public void ftpServerStarted() {

        try {
            ftpaddress.setText("  ftp://" + Utils.getInetAddressFromString(runAsRoot()).trim() + ":"+Utils.ChangeSettings.getPortNumber(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpstartstop.setText("Stop FTP");
        router_animation.playAnimation();
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        else
        super.onBackPressed();
    }

    @Override
    public void ftpServerStopped() {
        ftpaddress.setText("");
        ftpstartstop.setText("Start FTP");
        router_animation.setProgress(0f);
        router_animation.pauseAnimation();
    }

    @Override
    public void isFtpServerRunning(boolean isRunning) {
        if (isRunning) {
            router_animation.playAnimation();
            try {
                ftpaddress.setText("ftp://"+"YourIP:"+Utils.ChangeSettings.getPortNumber(this));
                ftpaddress.setText("  ftp://" + Utils.getInetAddressFromString(runAsRoot()).trim() +":"+Utils.ChangeSettings.getPortNumber(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ftpstartstop.setText("Stop FTP");
        } else {
            router_animation.pauseAnimation();
            ftpaddress.setText("");
            ftpstartstop.setText("Start FTP");
        }
    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("IPADDRESS", "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IPADDRESS", ex.toString());
        }
        return null;
    }



    public static String getMobileIPAddress() {


            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    Log.d("DISPLAY NAME",intf.getDisplayName());
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            return  addr.getHostAddress();
                        }
                    }
                }
            } catch (Exception ex) { } // for now eat exceptions
            return "";

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MainActivity.this, HowToUse.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isMobile",isChecked).apply();
        snackbar.dismiss();
    }
}
