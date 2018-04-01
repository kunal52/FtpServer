package com.techweblearn.www.ftpserver;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Created by Kunal on 21-11-2017.
 */

public class Utils {



    public static class ChangeSettings
    {

        public static void enabledisableAnonymousUser(boolean b)
        {
            replaceSettingData(".anonymous.enableflag",String.valueOf(b));
        }

        public static void changeAnonyIdleTime(String time)
        {
            replaceSettingData("anonymous.idletime",time);
        }

        public static void changeAnonyWrite(boolean b)
        {
            replaceSettingData("anonymous.writepermission",String.valueOf(b));
        }

        public static void rwPath(String path)
        {
            replaceSettingData("anonymous.homedirectory",path);
        }

        public static void changeAnonyMaxConnection(String connection)
        {

            replaceSettingData("anonymous.maxloginnumber",connection);

        }

        public static void changeAnonyMaxConnectionPerIP(String connection)
        {

            replaceSettingData("anonymous.maxloginperip", connection);

        }

        public static void changeAnonyMaxUpSpeed(String speed)
        {

                replaceSettingData("anonymous.uploadrate", speed);

        }


        public static void changeAnonyMaxDownSpeed(String speed)
        {
            replaceSettingData("anonymous.downloadrate", speed);
        }


        public static void enabledisableAdminUser(boolean bool)
        {
            replaceSettingData("admin.enableflag", String.valueOf(bool));
        }


        public static void changeAdminPassword(String password)
        {
            PropertiesUserManagerFactory userManagerFactory=new PropertiesUserManagerFactory();
            String pass=userManagerFactory.getPasswordEncryptor().encrypt(password);
            replaceSettingData("admin.userpassword",pass);
        }

        public static void changeAdminIdleConnectionTime(String time)
        {
            replaceSettingData("admin.idletime",time);
        }

        public static void changeAdminWrite(boolean b)
        {
            replaceSettingData("admin.writepermission", String.valueOf(b));
        }

        public static void changeAdminRWPath(String path)
        {
            replaceSettingData("admin.homedirectory",path);
        }

        public static void changeAdminMaxDownSpeed(String speed)
        {
            replaceSettingData("user.admin.downloadrate",speed);
        }
        public static void changeAdminMaxUpSpeed(String speed)
        {
            replaceSettingData("admin.uploadrate",speed);
        }
        public static void changeAdminMaxConnection(String connection)
        {
            replaceSettingData("admin.maxloginnumber",connection);
        }
        public static void changeAdminMaxConnectionPerIp(String connection)
        {
            replaceSettingData("admin.maxloginperip",connection);
        }

        public static boolean isPortInRange(int port)
        {
            if(port > 1023 && port < 65535)
                return true;
            return false;
        }


        public static int getPortNumber(Context context)
        {
            return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("port_no", String.valueOf(2345)));
        }







        public static void replaceSettingData(String from,String news)
        {
            try {
                String lineReplace;
                File in=new File("/data/user/0/com.techweblearn.www.ftpserver/cache","users.properties");
                File out=new File("/data/user/0/com.techweblearn.www.ftpserver/cache","usersnew.properties");
                out.createNewFile();

                BufferedWriter bw=new BufferedWriter(new FileWriter(out));


                char[]ch=new char[100];
                String line;
                BufferedReader b=new BufferedReader(new FileReader(in));
                while((line=b.readLine())!=null)
                {
                    if(line.contains(from))
                    {
                        System.out.println(line);
                        int u=line.indexOf("=");
                        line.getChars(u+1, line.length(), ch, 0);
                        lineReplace=String.valueOf(ch);
                        System.out.println(lineReplace);

                        line=line.replace(lineReplace.trim(), news);
                        System.out.println(line);
                    }

                    bw.write(line+"\n");
                }
                b.close();
                bw.flush();
                bw.close();

                in.delete();
                out.renameTo(in);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public static void copyFileFromAsssets(String filename,Context context)
    {
        boolean b =PreferenceManager.getDefaultSharedPreferences(context).getBoolean("once",true);
        if(b) {
            try {
                InputStream inputStream = context.getAssets().open(filename);
                File file = new File("/data/user/0/com.techweblearn.www.ftpserver/cache","users.properties");
                file.createNewFile();
                byte[] buffer = new byte[5120];
                FileOutputStream outputStream = new FileOutputStream(file);
                int length;
                length = inputStream.read(buffer);
                outputStream.write(buffer, 0, length);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("once",false).apply();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File createFileFromInputStream(String filename, Context context)
    {
        try{
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(filename);

            File f = new File(context.getCacheDir(),filename);

            File out=new File("/storage/emulated/0/Android/data/"+context.getPackageName()+"/users.properties");
            if(!out.exists())
                out.createNewFile();

            OutputStream write=new FileOutputStream(out);

            OutputStream outputStream = new FileOutputStream(f);


            byte buffer[] = new byte[2048];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
                write.write(buffer,0,length);
            }

            write.close();
            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String runAsRoot() {
        try {
            Process process = Runtime.getRuntime().exec("ifconfig wlan0");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean isWifiApEnable(Context context)
    {
        WifiManager wifiManager= (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Method method;
        try {
            method=wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(wifiManager, (Object) null);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean enableWifiAp(Context context)
    {
        WifiManager wifiManager= (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
          //  method.invoke(wifiManager, null, true);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isWifiEnable(Context context)
    {
        WifiManager wifiManager= (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
            return true;
        else return false;
    }

    public static void disableWifiAp(Context context)
    {
/*
        WifiManager wifiManager= (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(isWifiApEnable(context)) {
            wifiManager.setWifiEnabled(false);
        }*/

    }


    public static String getInetAddressFromString(String s) throws Exception
    {
        Log.d("IPCONFIG",s);
        try {
        String[]s1=s.split("inet addr:");
        String []s3=s1[1].split("Bcast:");
        return s3[0];
        }catch (Exception e)
        {
            String[] s1 = s.split("ip");
            String[] s2 = s1[1].split("mask");
            return s2[0].trim();
        }
    }



    public static void requestPermission(Activity thisActivity)
    {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(thisActivity);
        }


        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.CHANGE_WIFI_STATE)) {

            } else {

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        11);
            }
        }


    }





}
