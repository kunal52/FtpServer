package com.techweblearn.www.ftpserver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;

import java.util.Map;


/**
 * Created by Kunal on 22-11-2017.
 */

public class SettingActivity extends PreferenceActivity {

    public static final int FOLDERPICKER_CODE =220;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceClickListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_setting);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
            Preference anonymous=getPreferenceScreen().findPreference("anonymous_path_to_share");
            Preference admin=getPreferenceScreen().findPreference("admin_path_to_share");

            admin.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("admin_path_to_share","/storage/emulated/0/"));
            anonymous.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("anonymous_path_to_share","/storage/emulated/0/"));
            anonymous.setOnPreferenceClickListener(this);
            admin.setOnPreferenceClickListener(this);

        }


        @Override
        public void onResume() {
            super.onResume();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


            switch (key)
            {
                case "anonymous_login":

                    boolean enable_anonymous=sharedPreferences.getBoolean("anonymous_login",true);
                    Utils.ChangeSettings.enabledisableAnonymousUser(enable_anonymous);
                    break;

                case "anonymous_path_to_share":


                    break;

                case "write__anonymous":
                    boolean write_anonymous= sharedPreferences.getBoolean(key,true);
                    Utils.ChangeSettings.changeAnonyWrite(write_anonymous);
                    break;

                case "anonymous_up_speedlimit":
                    String anonymous_up_speedlimit=sharedPreferences.getString("anonymous_up_speedlimit","0");
                    Utils.ChangeSettings.changeAnonyMaxUpSpeed(anonymous_up_speedlimit);
                    break;

                case "anonymous_down_speedlimit":
                    String anonymous_down_speedlimit=sharedPreferences.getString("anonymous_down_speedlimit","0");
                    Utils.ChangeSettings.changeAnonyMaxDownSpeed(anonymous_down_speedlimit);
                    break;

                case "anonymous_max_connection":
                    String anonymous_max_connection=sharedPreferences.getString("anonymous_max_connection","0");
                    Utils.ChangeSettings.changeAnonyMaxConnection(anonymous_max_connection);
                    break;

                case "anonymous_max_sim_conenction":
                    String anonymous_max_sim_conenction=sharedPreferences.getString("anonymous_max_sim_conenction","0");
                    Utils.ChangeSettings.changeAnonyMaxConnectionPerIP(anonymous_max_sim_conenction);
                    break;

                case "admin_login":

                    boolean admin_login=sharedPreferences.getBoolean("admin_login",true);
                    Utils.ChangeSettings.enabledisableAdminUser(admin_login);

                    break;

                case "admin_password":

                    String admin_password=sharedPreferences.getString("admin_password","admin");
                    Utils.ChangeSettings.changeAdminPassword(admin_password);

                    break;

                case "admin_write":

                    boolean admin_write=sharedPreferences.getBoolean("admin_write",true);
                    Utils.ChangeSettings.changeAdminWrite(admin_write);

                    break;

                case "admin_path_to_share":
                    break;

                case "admin_up_max_speed":

                    String admin_up_max_speed=sharedPreferences.getString("admin_up_max_speed","0");
                    Utils.ChangeSettings.changeAdminMaxUpSpeed(admin_up_max_speed);

                    break;

                case "admin_down_speed_limit":


                    String admin_down_max=sharedPreferences.getString("admin_down_speed_limit","0");
                    Utils.ChangeSettings.changeAdminMaxDownSpeed(admin_down_max);

                    break;

                case "admin_max_connection":


                    String admin_max_connection=sharedPreferences.getString("admin_max_connection","16");
                    Utils.ChangeSettings.changeAdminMaxConnection(admin_max_connection);

                    break;

                case "admin_max_ip_connection":

                    String admin_max_ip_connection=sharedPreferences.getString("admin_max_ip_connection","4");
                    Utils.ChangeSettings.changeAdminMaxConnectionPerIp(admin_max_ip_connection);
                    break;

                case "port_no":
                    String port_no=sharedPreferences.getString("port_no","2345");
                    if(!Utils.ChangeSettings.isPortInRange(Integer.parseInt(port_no)))
                    {
                        Toast.makeText(getActivity(),"Port No is not in Between 1023-65535",Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(),"Default Port 2345 is Used",Toast.LENGTH_LONG).show();
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(key,"2345").apply();
                        port_no="2345";
                    }

                    Map<String, ?> preferencesMap = sharedPreferences.getAll();
                    Object changedPreference = preferencesMap.get(key);
                    if (preferencesMap.get(key) instanceof EditTextPreference) {
                        upDatePreferenceSummary((EditTextPreference) changedPreference,port_no);
                    }

            }

            Toast.makeText(getActivity(),"Stop Start FTP To See Changes",Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equals("anonymous_path_to_share"))
            {

                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(getActivity())
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .build();

                chooser.show();

                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String folderLocation) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("anonymous_path_to_share",folderLocation).apply();
                        Preference anonymous=getPreferenceScreen().findPreference("anonymous_path_to_share");
                        Utils.ChangeSettings.rwPath(folderLocation);
                        anonymous.setSummary(folderLocation);
                    }
                });
            }

            if(preference.getKey().equals("admin_path_to_share"))
            {

                StorageChooser chooser = new StorageChooser.Builder()
                        .withActivity(getActivity())
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .build();

                chooser.show();

                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String folderLocation) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("admin_path_to_share",folderLocation).apply();
                        Preference anonymous=getPreferenceScreen().findPreference("admin_path_to_share");
                        Utils.ChangeSettings.changeAdminRWPath(folderLocation);
                        anonymous.setSummary(folderLocation);
                    }
                });
            }

            return false;
        }
    }


    private static void upDatePreferenceSummary(EditTextPreference preference,String s)
    {
        preference.setSummary(s);
        preference.setText(s);
    }


}
