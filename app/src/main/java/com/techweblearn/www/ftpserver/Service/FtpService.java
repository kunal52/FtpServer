package com.techweblearn.www.ftpserver.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.techweblearn.www.ftpserver.Activity.MainActivity;
import com.techweblearn.www.ftpserver.R;
import com.techweblearn.www.ftpserver.Utils;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.impl.USER;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultDataConnectionConfiguration;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;

import static com.techweblearn.www.ftpserver.Utils.runAsRoot;

public class FtpService extends Service {


    FtpServerFactory serverFactory;
    ListenerFactory factory;
    PropertiesUserManagerFactory userManagerFactory;
    File userproperties;
    FtpServer ftpServer;
    NotificationManager notificationManager;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.

    public FtpService() {

    }

    @Override
    public void onCreate() {
        serverFactory=new FtpServerFactory();
        factory=new ListenerFactory();
        userManagerFactory=new PropertiesUserManagerFactory();



        notificationManager= (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"Ftp Server",NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);
        }

        factory.setPort(2438);
        serverFactory.addListener("default", factory.createListener());
        userproperties =new File("/data/user/0/com.techweblearn.www.ftpserver/cache","users.properties");
        userManagerFactory.setFile(userproperties);
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        ftpServer=serverFactory.createServer();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
        showNotification();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }




    @Override
    public void onDestroy() {
        cancelNotification();
        ftpServer.stop();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void showNotification() {

        try {
            Notification notification=null;
   PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);


            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isMobile",false))
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    notification =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.icon)  // the status icon// the status text// the time stamp
                                    .setContentTitle("FTP Server Started")  // the label of the entry
                                    .setContentText("ftp://" + "Your IP" + ":2438")
                                    .setOngoing(true)
                                    .setContentIntent(contentIntent)
                                    .setChannelId(CHANNEL_ID).build();
                }

                else {


                    notification = new Notification.Builder(this)
                            .setSmallIcon(R.drawable.icon)  // the status icon// the status text// the time stamp
                            .setContentTitle("FTP Server Started")  // the label of the entry
                            .setContentText("ftp://" + "Your IP" + ":2438")
                            .setOngoing(true)
                            .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                            .build();
                }else

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                 notification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.icon)  // the status icon// the status text// the time stamp
                                .setContentTitle("FTP Server Started")  // the label of the entry
                                .setContentText("ftp://" + Utils.getInetAddressFromString(runAsRoot()).trim() + ":2438")
                                .setOngoing(true)
                                .setContentIntent(contentIntent)
                                .setChannelId(CHANNEL_ID).build();
            }

            else {


                 notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)  // the status icon// the status text// the time stamp
                        .setContentTitle("FTP Server Started")  // the label of the entry
                        .setContentText("ftp://" + Utils.getInetAddressFromString(runAsRoot()).trim() + ":2438")
                        .setOngoing(true)
                        .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                        .build();
            }

        notificationManager.notify(10, notification);
        }catch (Exception e){}

    }


    public void cancelNotification() {
        notificationManager.cancel(10); // Notification ID to cancel
    }


}
