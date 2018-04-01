package com.techweblearn.www.ftpserver.Activity;

import android.content.Context;
import android.content.Intent;

import com.techweblearn.www.ftpserver.Interfaces.MainActivityModelInterface;
import com.techweblearn.www.ftpserver.Service.FtpService;
import com.techweblearn.www.ftpserver.Utils;

import static com.techweblearn.www.ftpserver.Utils.runAsRoot;

/**
 * Created by Kunal on 22-11-2017.
 */

public class MainActivityModel implements MainActivityModelInterface {
    Context context;

    public MainActivityModel(Context context) {
        this.context = context;
    }

    @Override
    public void onftpStartStop(OnFtpServerListener listener) {
        if(Utils.isMyServiceRunning(FtpService.class,context))
        {
            context.stopService(new Intent(context,FtpService.class));
            listener.ftpServerStopped();
        }
        else
        {
            try {
                Utils.getInetAddressFromString(runAsRoot());

            }catch (Exception e){
                listener.ftpServerStarted();
                return;
            }
            context.startService(new Intent(context,FtpService.class));
            listener.ftpServerStarted();
        }
    }

    @Override
    public void checkFtpIsRunning(OnFtpServerListener listener) {
        if(Utils.isMyServiceRunning(FtpService.class,context))
            listener.isFtpServerRunning(true);
        else listener.isFtpServerRunning(false);
    }
}
