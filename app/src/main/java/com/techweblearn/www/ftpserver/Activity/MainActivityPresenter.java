package com.techweblearn.www.ftpserver.Activity;

import android.content.Context;

import com.techweblearn.www.ftpserver.Interfaces.MainActivityModelInterface;
import com.techweblearn.www.ftpserver.Interfaces.MainActivityPresenterInterface;
import com.techweblearn.www.ftpserver.Interfaces.MainActivityViewInterface;

/**
 * Created by Kunal on 22-11-2017.
 */

public class MainActivityPresenter implements MainActivityPresenterInterface,MainActivityModelInterface.OnFtpServerListener {


    MainActivityModelInterface modelInterface;
    MainActivityViewInterface viewInterface;

    public MainActivityPresenter(Context context,MainActivityViewInterface viewInterface) {
        this.viewInterface = viewInterface;
        modelInterface=new MainActivityModel(context);
    }

    @Override
    public void onftpStartStop() {
        modelInterface.onftpStartStop(this);
    }

    @Override
    public void checkFtpIsRunning() {
        modelInterface.checkFtpIsRunning(this);
    }

    @Override
    public void ftpServerStarted() {
        viewInterface.ftpServerStarted();
    }

    @Override
    public void ftpServerStopped() {
        viewInterface.ftpServerStopped();
    }

    @Override
    public void isFtpServerRunning(boolean isRunning) {
        viewInterface.isFtpServerRunning(isRunning);
    }
}
