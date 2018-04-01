package com.techweblearn.www.ftpserver.Interfaces;

/**
 * Created by Kunal on 22-11-2017.
 */

public interface MainActivityModelInterface {

    interface OnFtpServerListener
    {
        void ftpServerStarted();
        void ftpServerStopped();
        void isFtpServerRunning(boolean isRunning);
    }

    void onftpStartStop(OnFtpServerListener listener);
    void checkFtpIsRunning(OnFtpServerListener listener);

}
