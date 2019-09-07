package com.alastair.checklink;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;

public class CheckLinkRunner implements Callable<String> {
    private Context theContext = null;
    private boolean shutdown = false;

    public void setShutdown() {
        shutdown = true;
    }

    public CheckLinkRunner(Context theContext) {
        this.theContext = theContext;
    }

    @Override
    public String call(){
        //setContentView(R.layout.activity_main);
        LinkState currState = LinkState.UNKNOWN;
        try {
            int i = 0;
            while (!shutdown) {
                    //System.out.println("Checking connection now" + i++);
                    LinkState stateNow = (isNetworkConnected() && getNetworkType().equals("WIFI") && canPing()) ? LinkState.UP : LinkState.DOWN;
                    //System.out.println("stateNow: "+stateNow+" currState:  " + currState);
                    if (currState != stateNow) {
                        writeChangedStateToFile(stateNow);
                        currState = stateNow;
                    }
                    sleep(10000);
                }

        } catch ( InterruptedException ie) {
            //System.out.println("InterruptedException :"+ie.getMessage() );
        }
        return("");

    }
/**
    public boolean sendFileToDropBox() {
        final String APP_KEY = "uqd8r5y042cvdt5";
        final String APP_SECRET = "8jw0rlw1hts2bf6";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig(
                "JavaTutorial/1.0", Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
    }

*/

    private boolean canPing() {
        boolean canPing = false;
        try {
            InetAddress in = InetAddress.getByName("192.168.0.1");
            //System.out.println("Trying to reach the router");
            if (in.isReachable(5000)) {
                //System.out.println("We can reach the router");
                canPing = true;
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return( canPing );
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) theContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    public String getNetworkType() {
        ConnectivityManager cm =
                (ConnectivityManager) theContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return "Was Null";
    }

    private void writeChangedStateToFile(LinkState state) {
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            //File file = new File("/storage/emulated/0/download/CheckLink-Phone.log"); //"/data/user/0/com.alastair.checklink/files/CheckLink-Phone.log");

            //File newFile = new File(Environment.DIRECTORY_DOWNLOADS, "CheckLink-Phone.log"); //"/data/user/0/com.alastair.checklink/files/CheckLink-Phone.log");
            File newFile = new File("/sdcard/Download/CheckLink-Phone.log");

            FileWriter fileWriter = new FileWriter(newFile, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " Link has changed and is now :" + state);
            printWriter.close();
            fileWriter.close();

        } catch ( IOException ioe) {
            //System.out.println("Error with the file handlng :"+ioe.getMessage());
            //System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " Link has changed and is now :" + state);
        }
    }
    /*
     * Overriding default InetAddress.isReachable() method to add 2 more arguments port and timeout value
     *
     * Address: www.google.com
     * port: 80 or 443
     * timeout: 2000 (in milliseconds)
     */
    private static boolean crunchifyAddressReachable(String address, int port, int timeout) {
        try {

            try (Socket crunchifySocket = new Socket()) {
                // Connects this socket to the server with a specified timeout value.
                crunchifySocket.connect(new InetSocketAddress(address, port), timeout);
            }
            // Return true if connection successful
            return true;
        } catch (IOException exception) {
            //exception.printStackTrace();

            // Return false if connection fails
            return false;
        }
    }

    private void logToFile(String theLogStatement) {
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            Context myContext = theContext;
            File file = new File(myContext.getFilesDir(), "CheckLink-Phone.log");
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + theLogStatement);
            printWriter.close();
            fileWriter.close();
        } catch ( IOException ioe) {
            //System.out.println("Error with the file handlng :"+ioe.getMessage());
            //System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + theLogStatement);
        }
    }
}
